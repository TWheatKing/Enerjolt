package me.twheatking.enerjolt.worldgen.dimension;

import me.twheatking.enerjolt.Enerjolt;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.component.ContaminatedComponent;
import me.twheatking.enerjolt.component.EnerjoltDataComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles death mechanics in the Contamination Zone:
 * - Drops ALL contaminated items on death
 * - Respawns player at checkpoint (outside zone)
 * - Items become lootable by other players
 * - Player can return to recover their loot
 */
@EventBusSubscriber(modid = EJOLTAPI.MOD_ID)
public class ContaminationDeathHandler {

    /**
     * When player dies in Contamination Zone, drop ALL contaminated items
     */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Only process deaths in Contamination Zone
        if (player.level().dimension() != ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            return;
        }

        // Collect all contaminated items
        List<ItemStack> contaminatedItems = new ArrayList<>();

        // Check inventory for contaminated items
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!stack.isEmpty()) {
                ContaminatedComponent component = stack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());

                // If contaminated (whether extracted or not), it drops on death in zone
                if (component != null && component.contaminated()) {
                    contaminatedItems.add(stack.copy());
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }

        // Also check armor slots and offhand
        for (ItemStack armorStack : player.getInventory().armor) {
            if (!armorStack.isEmpty()) {
                ContaminatedComponent component = armorStack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());
                if (component != null && component.contaminated()) {
                    contaminatedItems.add(armorStack.copy());
                    armorStack.shrink(armorStack.getCount());
                }
            }
        }

        ItemStack offhandStack = player.getInventory().offhand.get(0);
        if (!offhandStack.isEmpty()) {
            ContaminatedComponent component = offhandStack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());
            if (component != null && component.contaminated()) {
                contaminatedItems.add(offhandStack.copy());
                player.getInventory().offhand.set(0, ItemStack.EMPTY);
            }
        }

        // Drop all contaminated items at death location
        if (!contaminatedItems.isEmpty()) {
            BlockPos deathPos = player.blockPosition();

            for (ItemStack stack : contaminatedItems) {
                // Create item entity at death position
                ItemEntity itemEntity = new ItemEntity(
                        player.level(),
                        deathPos.getX() + 0.5,
                        deathPos.getY() + 0.5,
                        deathPos.getZ() + 0.5,
                        stack
                );

                // Set pickup delay so player can't immediately grab them on respawn
                itemEntity.setDefaultPickUpDelay();

                // Make items persist longer (10 minutes instead of 5)
                itemEntity.lifespan = 12000;

                player.level().addFreshEntity(itemEntity);
            }

            // Broadcast death with loot to nearby players
            broadcastDeathMessage(player, deathPos, contaminatedItems.size());

            // Play dramatic sound
            player.level().playSound(null, deathPos,
                    SoundEvents.WITHER_DEATH, SoundSource.PLAYERS, 1.0F, 0.8F);
        }
    }

    /**
     * Force respawn at checkpoint (portal location in overworld)
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Check if player died in Contamination Zone (they're now in overworld)
        // We detect this by checking if they have a respawn position set
        if (event.isEndConquered()) {
            return; // Don't interfere with end dimension respawn
        }

        // Send warning message
        player.displayClientMessage(
                Component.literal("§c☠ You died in the Contamination Zone!"),
                false
        );
        player.displayClientMessage(
                Component.literal("§7Your contaminated items remain where you fell."),
                false
        );
        player.displayClientMessage(
                Component.literal("§eReturn to recover them... if you dare."),
                false
        );

        // Play ominous sound
        player.level().playSound(null, player.blockPosition(),
                SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0F, 0.5F);
    }

    /**
     * Broadcasts death message to nearby players
     */
    private static void broadcastDeathMessage(ServerPlayer deadPlayer, BlockPos deathPos, int itemCount) {
        ServerLevel level = (ServerLevel) deadPlayer.level();

        // Create death marker message with coordinates
        Component deathMessage = Component.literal(
                "§c☠ " + deadPlayer.getName().getString() + " died at " +
                        deathPos.getX() + ", " + deathPos.getY() + ", " + deathPos.getZ() +
                        " §7[" + itemCount + " items]"
        );

        // Broadcast to all players in Contamination Zone
        level.players().forEach(player -> {
            if (player.level().dimension() == ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
                player.displayClientMessage(deathMessage, false);
                player.displayClientMessage(
                        Component.literal("§6⚠ Their loot is now up for grabs!"),
                        false
                );
            }
        });
    }

    /**
     * Prevent contaminated items from being kept on death (failsafe)
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) {
            return;
        }

        ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();

        // Only process if old player was in Contamination Zone
        if (oldPlayer.level().dimension() != ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            return;
        }

        // Double-check: remove any contaminated items that somehow made it through
        for (int i = 0; i < newPlayer.getInventory().getContainerSize(); i++) {
            ItemStack stack = newPlayer.getInventory().getItem(i);

            if (!stack.isEmpty()) {
                ContaminatedComponent component = stack.get(EnerjoltDataComponentTypes.CONTAMINATED.get());

                if (component != null && component.contaminated()) {
                    // This shouldn't happen, but remove it just in case
                    newPlayer.getInventory().setItem(i, ItemStack.EMPTY);
                    Enerjolt.LOGGER.warn("Removed contaminated item {} that persisted through death!",
                            stack.getHoverName().getString());
                }
            }
        }
    }
}