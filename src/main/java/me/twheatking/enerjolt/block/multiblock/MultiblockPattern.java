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
    public enum Shape {
        CUBE,
        DOME
    }

    private final int size; // 5 or 7
    private final BlockPos controllerPos;
    private final Direction facing;
    private final Level level;
    private Shape shape = Shape.CUBE; // Default to cube

    // Structure validation results
    private boolean isValid = false;
    private List<BlockPos> missingBlocks = new ArrayList<>();
    private List<BlockPos> glassPositions = new ArrayList<>();
    private List<BlockPos> suggestedPositions = new ArrayList<>(); // Suggestions for hologram
    private BlockPos centerPos;

    public MultiblockPattern(Level level, BlockPos controllerPos, Direction facing, int size) {
        this.level = level;
        this.controllerPos = controllerPos;
        this.facing = facing;
        this.size = size;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }

    /**
     * Validates the multiblock structure
     * @param requirePlant If true, checks for dirt + planted sapling/crop in center
     * @return true if structure is valid
     */
    public boolean validate(boolean requirePlant) {
        missingBlocks.clear();
        glassPositions.clear();
        suggestedPositions.clear();
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

        // ONLY check the base (y=0) - walls and roof are optional/suggestions
        int glassCount = 0;

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                BlockPos checkPos = getWorldPos(x, 0, z); // Only y=0 (base)

                // Skip controller position
                if (checkPos.equals(controllerPos)) continue;

                // Skip center bottom (dirt)
                if (checkPos.equals(centerPos)) continue;

                BlockState state = level.getBlockState(checkPos);

                // Check if this should be a base frame position
                if (isBaseFramePosition(x, z)) {
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

        // Calculate suggested positions for walls/roof (for hologram only)
        calculateSuggestedPositions();

        // Must have at least 8 glass blocks in base
        if (glassCount < 8) {
            isValid = false;
            return false;
        }

        isValid = missingBlocks.isEmpty();
        return isValid;
    }

    /**
     * Calculate suggested positions for walls and roof (for hologram visualization)
     */
    private void calculateSuggestedPositions() {
        suggestedPositions.clear();

        if (shape == Shape.CUBE) {
            // Add all frame positions except base
            for (int x = 0; x < size; x++) {
                for (int y = 1; y < size; y++) { // Start from y=1 (above base)
                    for (int z = 0; z < size; z++) {
                        if (isFramePosition(x, y, z)) {
                            BlockPos pos = getWorldPos(x, y, z);
                            if (!pos.equals(centerPos.above())) { // Don't suggest over plant
                                suggestedPositions.add(pos);
                            }
                        }
                    }
                }
            }
        } else if (shape == Shape.DOME) {
            // Add dome-shaped suggestions
            int radius = size / 2;
            int centerX = size / 2;
            int centerZ = size / 2;

            for (int x = 0; x < size; x++) {
                for (int z = 0; z < size; z++) {
                    // Calculate distance from center
                    double dx = x - centerX;
                    double dz = z - centerZ;
                    double distFromCenter = Math.sqrt(dx * dx + dz * dz);

                    if (distFromCenter <= radius + 0.5) {
                        // Calculate dome height at this position
                        double normalizedDist = distFromCenter / radius;
                        int domeHeight = (int) Math.round(radius * Math.sqrt(1 - normalizedDist * normalizedDist));

                        // Add vertical wall/roof positions up to dome height
                        for (int y = 1; y <= domeHeight; y++) {
                            // Only add edge positions for walls, fill in top for dome
                            if (y == domeHeight || isEdgePosition(x, z)) {
                                BlockPos pos = getWorldPos(x, y, z);
                                if (!pos.equals(centerPos.above())) {
                                    suggestedPositions.add(pos);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if position is on the edge (for dome walls)
     */
    private boolean isEdgePosition(int x, int z) {
        return x == 0 || x == size - 1 || z == 0 || z == size - 1;
    }

    /**
     * Checks if a position is part of the base frame
     */
    private boolean isBaseFramePosition(int x, int z) {
        // Base frame is the outer perimeter at y=0
        return x == 0 || x == size - 1 || z == 0 || z == size - 1;
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
    public List<BlockPos> getSuggestedPositions() { return suggestedPositions; }
    public BlockPos getCenterPos() { return centerPos; }
    public int getSize() { return size; }

    /**
     * Sets client-side data from sync packet (client only)
     */
    public void setClientData(boolean isValid, List<BlockPos> missingBlocks, List<BlockPos> glassPositions, BlockPos centerPos) {
        this.isValid = isValid;
        this.missingBlocks = new ArrayList<>(missingBlocks);
        this.glassPositions = new ArrayList<>(glassPositions);
        this.centerPos = centerPos;
        // Recalculate suggestions on client
        calculateSuggestedPositions();
    }

    /**
     * Gets all frame positions for hologram rendering
     * This includes both required (base) and suggested (walls/roof) positions
     */
    public List<BlockPos> getAllFramePositions() {
        List<BlockPos> positions = new ArrayList<>();

        // Add base positions (required)
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                if (isBaseFramePosition(x, z)) {
                    BlockPos pos = getWorldPos(x, 0, z);
                    if (!pos.equals(controllerPos)) {
                        positions.add(pos);
                    }
                }
            }
        }

        // Add suggested wall/roof positions
        positions.addAll(suggestedPositions);

        return positions;
    }
}