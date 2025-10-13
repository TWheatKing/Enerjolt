package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.component.ContaminatedComponent;
import me.twheatking.enerjolt.component.EnerjoltDataComponentTypes;
import me.twheatking.enerjolt.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Block entity that handles extraction mechanics:
 * - Stores contaminated loot for extraction
 * - Manages extraction timer (2 minutes default)
 * - Spawns waves of hostile mobs during extraction
 * - Processes loot on successful extraction
 */
public class ExtractionBeaconBlockEntity extends BlockEntity {

    // Extraction configuration
    private static final int EXTRACTION_TIME = 2400; // 120 seconds (2 minutes)
    private static final int WAVE_INTERVAL = 600; // 30 seconds between waves
    private static final int MOB_SPAWN_RADIUS = 20; // Blocks away from beacon
    private static final int MOBS_PER_WAVE = 5; // Mobs per wave

    // State
    private boolean extracting = false;
    private int extractionTimer = 0;
    private int waveTimer = 0;
    private int currentWave = 0;
    private UUID initiatingPlayer = null;

    // Inventory for contaminated items
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    public ExtractionBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(EnerjoltBlockEntities.EXTRACTION_BEACON_ENTITY.get(), pos, state);
    }

    // ===== TICK LOGIC =====

    public static void tick(Level level, BlockPos pos, BlockState state, ExtractionBeaconBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }

        if (!entity.extracting) {
            return;
        }

        entity.extractionTimer++;
        entity.waveTimer++;

        // Spawn wave
        if (entity.waveTimer >= WAVE_INTERVAL) {
            entity.spawnWave((ServerLevel) level, pos);
            entity.waveTimer = 0;
            entity.currentWave++;
        }

        // Complete extraction
        if (entity.extractionTimer >= EXTRACTION_TIME) {
            entity.completeExtraction((ServerLevel) level, pos);
        }

        entity.setChanged();
    }

    // ===== EXTRACTION CONTROL =====

    /**
     * Starts the extraction process
     */
    public boolean startExtraction(Player player) {
        if (extracting) {
            player.displayClientMessage(
                    Component.literal("§cExtraction already in progress!"),
                    true
            );
            return false;
        }

        // Check if in Contamination Zone
        if (level.dimension() != ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            player.displayClientMessage(
                    Component.literal("§cExtraction can only be called in the Contamination Zone!"),
                    true
            );
            return false;
        }

        // Check if there are items to extract
        if (items.stream().allMatch(ItemStack::isEmpty)) {
            player.displayClientMessage(
                    Component.literal("§cNo contaminated items to extract!"),
                    true
            );
            return false;
        }

        // Start extraction
        extracting = true;
        extractionTimer = 0;
        waveTimer = 0;
        currentWave = 0;
        initiatingPlayer = player.getUUID();

        // Notify nearby players
        broadcastMessage(Component.literal("§6⚠ EXTRACTION CALLED! DEFEND THE BEACON! ⚠"));
        broadcastMessage(Component.literal("§7Time remaining: 2:00"));

        // Play sound
        level.playSound(null, worldPosition, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 0.8F);

        setChanged();
        return true;
    }

    /**
     * Completes the extraction, marking all items as extracted
     */
    private void completeExtraction(ServerLevel level, BlockPos pos) {
        extracting = false;

        // Process all items - mark as extracted
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                ContaminatedComponent component = stack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());
                if (component != null && component.needsExtraction()) {
                    // Mark as extracted (safe to use)
                    stack.set(EnerjoltDataComponentTypes.CONTAMINATED.get(), ContaminatedComponent.EXTRACTED);
                }
            }
        }

        // Notify players
        broadcastMessage(Component.literal("§a✔ EXTRACTION COMPLETE! Items are now safe to use!"));

        // Play success sound
        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);

        setChanged();
    }

    // ===== WAVE SPAWNING =====

    /**
     * Spawns a wave of hostile mobs around the beacon
     */
    private void spawnWave(ServerLevel level, BlockPos pos) {
        currentWave++;

        // Notify players
        broadcastMessage(Component.literal("§c⚠ WAVE " + currentWave + " INCOMING!"));

        // Play warning sound
        level.playSound(null, pos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.0F, 0.8F);

        // Spawn mobs in a circle around the beacon
        for (int i = 0; i < MOBS_PER_WAVE; i++) {
            double angle = (2 * Math.PI * i) / MOBS_PER_WAVE;
            double x = pos.getX() + MOB_SPAWN_RADIUS * Math.cos(angle);
            double z = pos.getZ() + MOB_SPAWN_RADIUS * Math.sin(angle);
            double y = level.getHeight() + 1; // Spawn above ground

            BlockPos spawnPos = new BlockPos((int) x, (int) y, (int) z);

            // Spawn zombie (can be customized with different mobs based on wave)
            EntityType<?> mobType = getMobTypeForWave(currentWave);
            Monster mob = (Monster) mobType.create(level);

            if (mob != null) {
                mob.moveTo(x, y, z, 0, 0);
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                        net.minecraft.world.entity.MobSpawnType.EVENT, null);
                level.addFreshEntity(mob);
            }
        }
    }

    /**
     * Gets the mob type for the current wave (scales difficulty)
     */
    private EntityType<?> getMobTypeForWave(int wave) {
        return switch (wave) {
            case 1 -> EntityType.ZOMBIE;
            case 2 -> EntityType.SKELETON;
            case 3 -> EntityType.SPIDER;
            case 4 -> EntityType.CREEPER;
            default -> wave % 2 == 0 ? EntityType.ZOMBIE : EntityType.SKELETON;
        };
    }

    // ===== UTILITY =====

    /**
     * Broadcasts a message to all players near the beacon
     */
    private void broadcastMessage(Component message) {
        if (level instanceof ServerLevel serverLevel) {
            AABB area = new AABB(worldPosition).inflate(50);
            List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(Player.class, area);
            nearbyPlayers.forEach(player -> player.displayClientMessage(message, false));
        }
    }

    /**
     * Gets remaining extraction time in seconds
     */
    public int getRemainingTime() {
        return (EXTRACTION_TIME - extractionTimer) / 20;
    }

    /**
     * Gets extraction progress (0.0 to 1.0)
     */
    public float getProgress() {
        return extracting ? (float) extractionTimer / EXTRACTION_TIME : 0.0F;
    }

    // ===== INVENTORY ACCESS =====

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    // ===== DATA SYNC =====

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putBoolean("Extracting", extracting);
        tag.putInt("ExtractionTimer", extractionTimer);
        tag.putInt("WaveTimer", waveTimer);
        tag.putInt("CurrentWave", currentWave);
        if (initiatingPlayer != null) {
            tag.putUUID("InitiatingPlayer", initiatingPlayer);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        extracting = tag.getBoolean("Extracting");
        extractionTimer = tag.getInt("ExtractionTimer");
        waveTimer = tag.getInt("WaveTimer");
        currentWave = tag.getInt("CurrentWave");
        if (tag.hasUUID("InitiatingPlayer")) {
            initiatingPlayer = tag.getUUID("InitiatingPlayer");
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // Getters for screen
    public boolean isExtracting() {
        return extracting;
    }

    public int getCurrentWave() {
        return currentWave;
    }
}
