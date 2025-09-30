package me.twheatking.enerjolt.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.entity.IndustrialGreenhouseBlockEntity;
import me.twheatking.enerjolt.block.entity.PhotosyntheticChamberBlockEntity;
import me.twheatking.enerjolt.block.multiblock.MultiblockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = EJOLTAPI.MOD_ID, value = Dist.CLIENT)
public class HologramRenderer {

    // ======================== HOLOGRAM CUSTOMIZATION ========================

    // Ghost block transparency (0.0 = invisible, 1.0 = fully opaque)
    private static final float GHOST_BLOCK_ALPHA = 0.35f;

    // Block choices for different positions
    private static final BlockState CENTER_DIRT_BLOCK = Blocks.DIRT.defaultBlockState();
    private static final BlockState PLANT_BLOCK = Blocks.OAK_SAPLING.defaultBlockState();
    private static final BlockState GLASS_BLOCK = Blocks.GLASS.defaultBlockState();
    private static final BlockState FRAME_BLOCK = Blocks.IRON_BARS.defaultBlockState(); // Use iron bars for greenhouse look

    // Outline colors (R, G, B, Alpha) - values from 0.0 to 1.0
    private static final float[] MISSING_BLOCK_OUTLINE = {1.0f, 0.8f, 0.0f, 0.7f}; // Gold/yellow
    private static final float[] VALID_BLOCK_OUTLINE = {0.0f, 1.0f, 0.0f, 0.5f};   // Green
    private static final float[] CENTER_DIRT_OUTLINE = {0.4f, 0.2f, 0.0f, 0.8f};   // Brown
    private static final float[] PLANT_OUTLINE = {0.0f, 1.0f, 0.0f, 0.8f};         // Bright green

    // Render settings
    private static final boolean SHOW_GHOST_BLOCKS = true;        // Show actual block models
    private static final boolean SHOW_OUTLINES = true;             // Show colored outlines
    private static final boolean SHOW_VALID_BLOCK_OUTLINES = true; // Show green outlines on correct blocks

    // Advanced: Custom block choices based on position
    private static final boolean USE_VARIED_GLASS = true; // Use different glass types

    // ========================================================================

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        Level level = player.level();
        UUID playerId = player.getUUID();

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        Vec3 cameraPos = event.getCamera().getPosition();

        // Check nearby block entities for greenhouses showing holograms
        BlockPos playerPos = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-32, -32, -32),
                playerPos.offset(32, 32, 32))) {

            BlockEntity be = level.getBlockEntity(pos);
            MultiblockPattern pattern = null;
            boolean showingHologram = false;

            if (be instanceof IndustrialGreenhouseBlockEntity greenhouse) {
                pattern = greenhouse.getPattern();
                showingHologram = greenhouse.isShowingHologramFor(playerId);
            } else if (be instanceof PhotosyntheticChamberBlockEntity chamber) {
                pattern = chamber.getPattern();
                showingHologram = chamber.isShowingHologramFor(playerId);
            }

            if (showingHologram && pattern != null) {
                renderMultiblockHologram(poseStack, bufferSource, pattern, cameraPos, level);
            }
        }

        // CRITICAL: Flush all buffers to actually render
        bufferSource.endBatch();
    }

    private static void renderMultiblockHologram(PoseStack poseStack, MultiBufferSource bufferSource,
                                                 MultiblockPattern pattern, Vec3 cameraPos, Level level) {
        List<BlockPos> framePositions = pattern.getAllFramePositions();
        List<BlockPos> missingBlocks = pattern.getMissingBlocks();
        List<BlockPos> glassPositions = pattern.getGlassPositions();
        BlockPos centerPos = pattern.getCenterPos();

        if (centerPos == null) {
            return;
        }

        int glassCount = glassPositions.size();
        boolean needsMoreGlass = glassCount < 16;

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        RandomSource random = RandomSource.create();

        // Render ghost blocks for all positions
        for (BlockPos pos : framePositions) {
            BlockState existingState = level.getBlockState(pos);
            boolean isMissing = missingBlocks.contains(pos) || existingState.isAir();

            if (isMissing) {
                // Determine what block should be here
                BlockState ghostBlock = getGhostBlockState(pos, centerPos, needsMoreGlass, framePositions);

                if (ghostBlock != null && SHOW_GHOST_BLOCKS) {
                    renderGhostBlock(poseStack, bufferSource, dispatcher, pos, cameraPos, ghostBlock, random);
                }

                // Also render outline for missing blocks
                if (SHOW_OUTLINES) {
                    renderBlockOutline(poseStack, bufferSource, pos, cameraPos,
                            MISSING_BLOCK_OUTLINE[0], MISSING_BLOCK_OUTLINE[1],
                            MISSING_BLOCK_OUTLINE[2], MISSING_BLOCK_OUTLINE[3]);
                }
            } else {
                // Show green outline for valid blocks
                if (SHOW_OUTLINES && SHOW_VALID_BLOCK_OUTLINES) {
                    renderBlockOutline(poseStack, bufferSource, pos, cameraPos,
                            VALID_BLOCK_OUTLINE[0], VALID_BLOCK_OUTLINE[1],
                            VALID_BLOCK_OUTLINE[2], VALID_BLOCK_OUTLINE[3]);
                }
            }
        }

        // Highlight center with special rendering
        if (level.getBlockState(centerPos).isAir()) {
            if (SHOW_GHOST_BLOCKS) {
                renderGhostBlock(poseStack, bufferSource, dispatcher, centerPos, cameraPos, CENTER_DIRT_BLOCK, random);
            }
            if (SHOW_OUTLINES) {
                renderBlockOutline(poseStack, bufferSource, centerPos, cameraPos,
                        CENTER_DIRT_OUTLINE[0], CENTER_DIRT_OUTLINE[1],
                        CENTER_DIRT_OUTLINE[2], CENTER_DIRT_OUTLINE[3]);
            }
        }
        if (level.getBlockState(centerPos.above()).isAir()) {
            if (SHOW_GHOST_BLOCKS) {
                renderGhostBlock(poseStack, bufferSource, dispatcher, centerPos.above(), cameraPos, PLANT_BLOCK, random);
            }
            if (SHOW_OUTLINES) {
                renderBlockOutline(poseStack, bufferSource, centerPos.above(), cameraPos,
                        PLANT_OUTLINE[0], PLANT_OUTLINE[1],
                        PLANT_OUTLINE[2], PLANT_OUTLINE[3]);
            }
        }
    }

    private static BlockState getGhostBlockState(BlockPos pos, BlockPos centerPos, boolean needsMoreGlass, List<BlockPos> framePositions) {
        if (pos.equals(centerPos)) {
            return CENTER_DIRT_BLOCK;
        } else if (pos.equals(centerPos.above())) {
            return PLANT_BLOCK;
        } else if (needsMoreGlass && framePositions.indexOf(pos) % 3 == 0) {
            // Use varied glass types for a more interesting look
            if (USE_VARIED_GLASS) {
                int index = framePositions.indexOf(pos);
                return switch (index % 6) {
                    case 0 -> Blocks.GLASS.defaultBlockState();
                    case 1 -> Blocks.WHITE_STAINED_GLASS.defaultBlockState();
                    case 2 -> Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
                    case 3 -> Blocks.CYAN_STAINED_GLASS.defaultBlockState();
                    case 4 -> Blocks.LIME_STAINED_GLASS.defaultBlockState();
                    default -> Blocks.GLASS.defaultBlockState();
                };
            }
            return GLASS_BLOCK;
        } else {
            return FRAME_BLOCK;
        }
    }

    private static void renderGhostBlock(PoseStack poseStack, MultiBufferSource bufferSource,
                                         BlockRenderDispatcher dispatcher, BlockPos pos, Vec3 cameraPos,
                                         BlockState state, RandomSource random) {
        poseStack.pushPose();

        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;

        poseStack.translate(x, y, z);

        // Create a translucent vertex consumer wrapper
        VertexConsumer baseConsumer = bufferSource.getBuffer(RenderType.translucent());
        VertexConsumer translucentConsumer = new TranslucentVertexConsumer(baseConsumer, GHOST_BLOCK_ALPHA);

        // Render the block model with translucency
        dispatcher.renderSingleBlock(
                state,
                poseStack,
                bufferSource,
                0x00F000F0, // Full bright
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.translucent()
        );

        poseStack.popPose();
    }

    private static void renderBlockOutline(PoseStack poseStack, MultiBufferSource bufferSource,
                                           BlockPos pos, Vec3 cameraPos,
                                           float r, float g, float b, float a) {
        poseStack.pushPose();

        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;

        poseStack.translate(x, y, z);

        Matrix4f matrix = poseStack.last().pose();

        // Get fresh line consumer for this outline
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        float minX = 0.0f, minY = 0.0f, minZ = 0.0f;
        float maxX = 1.0f, maxY = 1.0f, maxZ = 1.0f;

        // Bottom face
        addLine(consumer, matrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        addLine(consumer, matrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        addLine(consumer, matrix, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        addLine(consumer, matrix, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        // Top face
        addLine(consumer, matrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        addLine(consumer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        addLine(consumer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        addLine(consumer, matrix, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        // Vertical edges
        addLine(consumer, matrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        addLine(consumer, matrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        addLine(consumer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        addLine(consumer, matrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);

        poseStack.popPose();
    }

    private static void addLine(VertexConsumer consumer, Matrix4f matrix,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float r, float g, float b, float a) {
        consumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setNormal(0, 1, 0);
        consumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setNormal(0, 1, 0);
    }

    /**
     * Wrapper to make blocks render with translucency
     */
    private static class TranslucentVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float alpha;

        public TranslucentVertexConsumer(VertexConsumer delegate, float alpha) {
            this.delegate = delegate;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            return delegate.addVertex(x, y, z);
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            return delegate.setColor(r, g, b, (int)(a * alpha));
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            return delegate.setUv(u, v);
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            return delegate.setUv1(u, v);
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            return delegate.setUv2(u, v);
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            return delegate.setNormal(x, y, z);
        }
    }
}