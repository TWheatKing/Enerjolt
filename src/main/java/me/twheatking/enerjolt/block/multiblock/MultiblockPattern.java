package me.twheatking.enerjolt.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiblockPattern {
    private final int size; // 5 or 7
    private final BlockPos controllerPos;
    private final Direction facing;
    private final Level level;

    // Structure validation results
    private boolean isValid = false;
    private List<BlockPos> missingBlocks = new ArrayList<>();
    private List<BlockPos> glassPositions = new ArrayList<>();
    private BlockPos centerPos;

    public MultiblockPattern(Level level, BlockPos controllerPos, Direction facing, int size) {
        this.level = level;
        this.controllerPos = controllerPos;
        this.facing = facing;
        this.size = size;
    }

    /**
     * Validates the multiblock structure
     * @param requirePlant If true, checks for dirt + planted sapling/crop in center
     * @return true if structure is valid
     */
    public boolean validate(boolean requirePlant) {
        missingBlocks.clear();
        glassPositions.clear();
        isValid = false;

        // Calculate center position (bottom center of the structure)
        centerPos = getCenterPosition();

        // Check center has dirt block
        BlockState centerState = level.getBlockState(centerPos);
        if (centerState.getBlock() != Blocks.DIRT && centerState.getBlock() != Blocks.GRASS_BLOCK) {
            missingBlocks.add(centerPos);
            return false;
        }

        // Check if sapling/crop is planted (if required)
        if (requirePlant) {
            BlockPos plantPos = centerPos.above();
            BlockState plantState = level.getBlockState(plantPos);
            if (!isValidPlant(plantState)) {
                missingBlocks.add(plantPos);
                return false;
            }
        }

        // Count glass blocks
        int glassCount = 0;

        // Check all positions in the cube
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    BlockPos checkPos = getWorldPos(x, y, z);

                    // Skip controller position
                    if (checkPos.equals(controllerPos)) continue;

                    // Skip center bottom (dirt) and above it (plant)
                    if (checkPos.equals(centerPos) || checkPos.equals(centerPos.above())) continue;

                    BlockState state = level.getBlockState(checkPos);

                    // Check if this should be a wall/frame position
                    if (isFramePosition(x, y, z)) {
                        if (state.isAir()) {
                            missingBlocks.add(checkPos);
                        } else {
                            // Check if it's glass
                            if (isGlassBlock(state.getBlock())) {
                                glassCount++;
                                glassPositions.add(checkPos);
                            }
                        }
                    }
                }
            }
        }

        // Must have at least 16 glass blocks
        if (glassCount < 16) {
            isValid = false;
            return false;
        }

        isValid = missingBlocks.isEmpty();
        return isValid;
    }

    /**
     * Checks if a position is part of the frame (walls, floor, ceiling)
     */
    private boolean isFramePosition(int x, int y, int z) {
        // Frame is any position on the outer shell
        return x == 0 || x == size - 1 ||
                y == 0 || y == size - 1 ||
                z == 0 || z == size - 1;
    }

    /**
     * Checks if a block is glass
     */
    private boolean isGlassBlock(Block block) {
        String blockId = block.toString().toLowerCase();
        return blockId.contains("glass") || block == Blocks.GLASS ||
                block == Blocks.TINTED_GLASS || blockId.contains("glass_pane");
    }

    /**
     * Checks if a block state is a valid plant (sapling or crop)
     */
    private boolean isValidPlant(BlockState state) {
        Block block = state.getBlock();
        String blockId = block.toString().toLowerCase();

        // Check for saplings
        if (blockId.contains("sapling")) return true;

        // Check for crops
        if (blockId.contains("wheat") || blockId.contains("carrot") ||
                blockId.contains("potato") || blockId.contains("beetroot")) return true;

        // Check common modded crops
        return block.defaultBlockState().hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_7) ||
                block.defaultBlockState().hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3);
    }

    /**
     * Converts local coordinates to world position
     */
    private BlockPos getWorldPos(int x, int y, int z) {
        // Calculate offset based on facing direction
        int offsetX = 0, offsetZ = 0;

        switch (facing) {
            case NORTH -> { // Controller on south side
                offsetX = x - size / 2;
                offsetZ = z;
            }
            case SOUTH -> { // Controller on north side
                offsetX = x - size / 2;
                offsetZ = -z;
            }
            case WEST -> { // Controller on east side
                offsetX = z;
                offsetZ = x - size / 2;
            }
            case EAST -> { // Controller on west side
                offsetX = -z;
                offsetZ = x - size / 2;
            }
        }

        return controllerPos.offset(offsetX, y, offsetZ);
    }

    /**
     * Gets the center bottom position of the structure
     */
    private BlockPos getCenterPosition() {
        int halfSize = size / 2;

        return switch (facing) {
            case NORTH -> controllerPos.offset(0, 0, halfSize);
            case SOUTH -> controllerPos.offset(0, 0, -halfSize);
            case WEST -> controllerPos.offset(halfSize, 0, 0);
            case EAST -> controllerPos.offset(-halfSize, 0, 0);
            default -> controllerPos;
        };
    }

    // Getters
    public boolean isValid() { return isValid; }
    public List<BlockPos> getMissingBlocks() { return missingBlocks; }
    public List<BlockPos> getGlassPositions() { return glassPositions; }
    public BlockPos getCenterPos() { return centerPos; }
    public int getSize() { return size; }

    /**
     * Gets all frame positions for hologram rendering
     */
    public List<BlockPos> getAllFramePositions() {
        List<BlockPos> positions = new ArrayList<>();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    if (isFramePosition(x, y, z)) {
                        BlockPos pos = getWorldPos(x, y, z);
                        if (!pos.equals(controllerPos)) {
                            positions.add(pos);
                        }
                    }
                }
            }
        }

        return positions;
    }
}