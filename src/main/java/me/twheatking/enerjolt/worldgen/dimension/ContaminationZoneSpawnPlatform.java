package me.twheatking.enerjolt.worldgen.dimension;

import me.twheatking.enerjolt.Enerjolt;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Generates a safe spawn platform when the Contamination Zone dimension loads
 * Ensures players don't spawn in the void or unsafe locations
 */
@EventBusSubscriber(modid = EJOLTAPI.MOD_ID)
public class ContaminationZoneSpawnPlatform {

    private static final BlockPos SPAWN_CENTER = new BlockPos(0, 64, 0);
    private static final int PLATFORM_RADIUS = 10;
    private static final int PLATFORM_HEIGHT = 3;

    private static boolean platformGenerated = false;

    /**
     * Generate spawn platform when dimension loads
     */
    @SubscribeEvent
    public static void onDimensionLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        // Only generate in Contamination Zone
        if (level.dimension() != ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            return;
        }

        // Only generate once per server session
        if (platformGenerated) {
            return;
        }

        Enerjolt.LOGGER.info("Generating Contamination Zone spawn platform...");
        generateSpawnPlatform(level);
        platformGenerated = true;
    }

    /**
     * Generates the actual platform structure
     */
    private static void generateSpawnPlatform(ServerLevel level) {
        BlockState platformBlock = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState wallBlock = Blocks.STONE_BRICK_WALL.defaultBlockState();
        BlockState lightBlock = Blocks.TORCH.defaultBlockState();
        BlockState portalBlock = EnerjoltBlocks.CONTAMINATION_PORTAL.get().defaultBlockState();

        // Generate main platform (circular)
        for (int x = -PLATFORM_RADIUS; x <= PLATFORM_RADIUS; x++) {
            for (int z = -PLATFORM_RADIUS; z <= PLATFORM_RADIUS; z++) {
                double distance = Math.sqrt(x * x + z * z);

                if (distance <= PLATFORM_RADIUS) {
                    BlockPos pos = SPAWN_CENTER.offset(x, 0, z);

                    // Base layer - solid platform
                    level.setBlock(pos, platformBlock, 3);

                    // Support pillars underneath
                    for (int y = -1; y >= -PLATFORM_HEIGHT; y--) {
                        level.setBlock(pos.below(-y), platformBlock, 3);
                    }

                    // Clear space above for spawning
                    for (int y = 1; y <= 4; y++) {
                        level.setBlock(pos.above(y), Blocks.AIR.defaultBlockState(), 3);
                    }

                    // Add wall around edge
                    if (distance >= PLATFORM_RADIUS - 1 && distance <= PLATFORM_RADIUS) {
                        level.setBlock(pos.above(), wallBlock, 3);
                    }

                    // Add torches for lighting (every 3 blocks on edge)
                    if (distance >= PLATFORM_RADIUS - 1.5 && distance <= PLATFORM_RADIUS - 0.5) {
                        if ((x + z) % 3 == 0) {
                            level.setBlock(pos.above(2), lightBlock, 3);
                        }
                    }
                }
            }
        }

        // Place portal in center for return trip
        BlockPos portalPos = SPAWN_CENTER.above();
        level.setBlock(portalPos, portalBlock, 3);

        // Add decorative pattern in center
        for (int i = 0; i < 8; i++) {
            double angle = (2 * Math.PI * i) / 8;
            int offsetX = (int) (3 * Math.cos(angle));
            int offsetZ = (int) (3 * Math.sin(angle));
            BlockPos decorPos = SPAWN_CENTER.offset(offsetX, 0, offsetZ);
            level.setBlock(decorPos, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 3);
        }

        // Add extraction beacons around the platform for easy access
        for (int i = 0; i < 4; i++) {
            double angle = (Math.PI / 2) * i + (Math.PI / 4); // 45, 135, 225, 315 degrees
            int offsetX = (int) (PLATFORM_RADIUS * 0.7 * Math.cos(angle));
            int offsetZ = (int) (PLATFORM_RADIUS * 0.7 * Math.sin(angle));
            BlockPos beaconPos = SPAWN_CENTER.offset(offsetX, 1, offsetZ);

            // Place extraction beacon
            level.setBlock(beaconPos, EnerjoltBlocks.EXTRACTION_BEACON.get().defaultBlockState(), 3);
        }

        // Add warning signs on walls (using sign blocks)
        placeWarningSign(level, SPAWN_CENTER.offset(0, 2, -PLATFORM_RADIUS + 1));
        placeWarningSign(level, SPAWN_CENTER.offset(0, 2, PLATFORM_RADIUS - 1));
        placeWarningSign(level, SPAWN_CENTER.offset(-PLATFORM_RADIUS + 1, 2, 0));
        placeWarningSign(level, SPAWN_CENTER.offset(PLATFORM_RADIUS - 1, 2, 0));

        Enerjolt.LOGGER.info("Contamination Zone spawn platform generated successfully!");
    }

    /**
     * Places a warning sign (just a skull for visual effect)
     */
    private static void placeWarningSign(ServerLevel level, BlockPos pos) {
        // Place a wither skeleton skull as warning
        level.setBlock(pos, Blocks.WITHER_SKELETON_SKULL.defaultBlockState(), 3);
    }

    /**
     * Can be called to manually regenerate platform if destroyed
     */
    public static void regeneratePlatform(ServerLevel level) {
        if (level.dimension() == ModDimensions.CONTAMINATION_ZONE_LEVEL_KEY) {
            Enerjolt.LOGGER.info("Manually regenerating Contamination Zone spawn platform...");
            generateSpawnPlatform(level);
        }
    }

    /**
     * Resets the generation flag (for testing or server restart)
     */
    public static void resetGenerationFlag() {
        platformGenerated = false;
    }
}