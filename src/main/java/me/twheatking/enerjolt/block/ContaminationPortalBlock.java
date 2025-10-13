package me.twheatking.enerjolt.block;

import me.twheatking.enerjolt.worldgen.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Portal block that serves as the checkpoint entry to the Contamination Zone.
 * Enforces inventory restrictions before allowing entry.
 */
public class ContaminationPortalBlock extends Block {

    public ContaminationPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        // Check if player is already in Contamination Zone
        if (level.dimension() == ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            // Teleport back to overworld
            teleportToOverworld(serverPlayer);
            return InteractionResult.SUCCESS;
        }

        // Check if inventory is empty (except armor and offhand)
        if (!isInventoryEmpty(serverPlayer)) {
            serverPlayer.displayClientMessage(
                    Component.literal("§c⚠ You must empty your inventory before entering the Contamination Zone!"),
                    true
            );
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.8F);
            return InteractionResult.FAIL;
        }

        // Teleport to Contamination Zone
        teleportToContaminationZone(serverPlayer);
        return InteractionResult.SUCCESS;
    }

    /**
     * Checks if player's main inventory is empty (allows armor/offhand)
     */
    private boolean isInventoryEmpty(ServerPlayer player) {
        // Check main inventory (slots 0-35: hotbar + main inventory)
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Teleports player to the Contamination Zone dimension
     */
    private void teleportToContaminationZone(ServerPlayer player) {
        ServerLevel contaminationZone = player.server.getLevel(ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY);

        if (contaminationZone == null) {
            player.displayClientMessage(
                    Component.literal("§cError: Contamination Zone dimension not found!"),
                    false
            );
            return;
        }

        // Find safe spawn position (spawn platform at Y=64)
        BlockPos spawnPos = new BlockPos(0, 64, 0);

        // Teleport with message
        player.displayClientMessage(
                Component.literal("§6⚠ ENTERING CONTAMINATION ZONE ⚠"),
                false
        );
        player.displayClientMessage(
                Component.literal("§7Death means losing all contaminated loot!"),
                false
        );

        // Play portal sound
        player.level().playSound(null, player.blockPosition(),
                SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 1.0F, 1.0F);

        // Teleport to dimension
        player.teleportTo(contaminationZone,
                spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                player.getYRot(), player.getXRot());

        // Spawn particles at destination
        contaminationZone.sendParticles(ParticleTypes.PORTAL,
                spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5,
                50, 0.5, 1.0, 0.5, 0.1);
    }

    /**
     * Teleports player back to overworld spawn
     */
    private void teleportToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);

        if (overworld == null) {
            return;
        }

        // Get world spawn
        BlockPos spawnPos = overworld.getSharedSpawnPos();

        player.displayClientMessage(
                Component.literal("§aExiting Contamination Zone..."),
                true
        );

        // Play portal sound
        player.level().playSound(null, player.blockPosition(),
                SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1.0F, 1.2F);

        // Teleport back
        player.teleportTo(overworld,
                spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                player.getYRot(), player.getXRot());
    }

    /**
     * Client-side particle effects
     */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // Spawn eerie particles around the portal
        if (random.nextInt(100) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }

        // Green/yellow contamination particles
        for (int i = 0; i < 4; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            double motionX = (random.nextDouble() - 0.5) * 0.1;
            double motionY = random.nextDouble() * 0.1;
            double motionZ = (random.nextDouble() - 0.5) * 0.1;

            // Mix of yellow and green particles for "contamination" effect
            if (random.nextBoolean()) {
                level.addParticle(ParticleTypes.FLAME, x, y, z, motionX, motionY, motionZ);
            } else {
                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, motionX, motionY, motionZ);
            }
        }
    }

    /**
     * Custom teleporter for handling dimension changes
     */
    private static class ContaminationZoneTeleporter implements net.minecraft.world.level.portal.DimensionTransition.PostDimensionTransition {
        private final BlockPos targetPos;

        public ContaminationZoneTeleporter(BlockPos targetPos) {
            this.targetPos = targetPos;
        }

        @Override
        public void onTransition(net.minecraft.world.entity.Entity entity) {
            if (entity instanceof ServerPlayer player) {
                // Teleport to exact position
                player.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);

                // Spawn particles at destination
                ((ServerLevel)player.level()).sendParticles(
                        ParticleTypes.PORTAL,
                        player.getX(), player.getY() + 1, player.getZ(),
                        50, 0.5, 1.0, 0.5, 0.1
                );
            }
        }
    }
}