package me.twheatking.enerjolt.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.twheatking.enerjolt.Enerjolt;
import me.twheatking.enerjolt.block.entity.IndustrialGreenhouseBlockEntity;
import me.twheatking.enerjolt.block.entity.PhotosyntheticChamberBlockEntity;
import me.twheatking.enerjolt.block.multiblock.MultiblockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
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
import org.joml.Matrix4f;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = Enerjolt.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class HologramRenderer {

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

        // Get the line buffer once for all outlines
        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.lines());

        // Render ghost blocks for all positions
        for (BlockPos pos : framePositions) {
            BlockState existingState = level.getBlockState(pos);
            boolean isMissing = missingBlocks.contains(pos) || existingState.isAir();

            if (isMissing) {
                // Determine what block should be here
                float r, g, b, a;

                if (pos.equals(centerPos)) {
                    r = 0.6f; g = 0.4f; b = 0.2f; a = 0.5f; // Brown for dirt
                } else if (pos.equals(centerPos.above())) {
                    r = 1.0f; g = 1.0f; b = 0.0f; a = 0.6f; // Yellow for plant
                } else if (needsMoreGlass && framePositions.indexOf(pos) % 3 == 0) {
                    r = 0.5f; g = 0.8f; b = 1.0f; a = 0.4f; // Light blue for glass
                } else {
                    r = 0.7f; g = 0.7f; b = 0.7f; a = 0.3f; // Gray for any block
                }

                // Draw filled outline instead of trying to render block model
                renderFilledBlock(poseStack, bufferSource, pos, cameraPos, r, g, b, a * 0.3f);
                renderBlockOutline(poseStack, lineConsumer, pos, cameraPos, r, g, b, a);
            } else {
                // Show green outline for valid blocks
                renderBlockOutline(poseStack, lineConsumer, pos, cameraPos, 0.0f, 1.0f, 0.0f, 0.5f);
            }
        }

        // Highlight center with special outline
        if (centerPos != null) {
            if (level.getBlockState(centerPos).isAir()) {
                renderBlockOutline(poseStack, lineConsumer, centerPos, cameraPos,
                        0.0f, 0.5f, 1.0f, 0.8f); // Blue
            }
            if (level.getBlockState(centerPos.above()).isAir()) {
                renderBlockOutline(poseStack, lineConsumer, centerPos.above(), cameraPos,
                        1.0f, 1.0f, 0.0f, 0.8f); // Yellow
            }
        }
    }

    private static void renderFilledBlock(PoseStack poseStack, MultiBufferSource bufferSource,
                                          BlockPos pos, Vec3 cameraPos,
                                          float r, float g, float b, float a) {
        poseStack.pushPose();

        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;

        poseStack.translate(x, y, z);

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());

        float min = 0.001f;
        float max = 0.999f;

        // Render all 6 faces
        // Bottom
        addQuad(consumer, matrix, min, min, min, max, min, min, max, min, max, min, min, max, r, g, b, a, 0, -1, 0);
        // Top
        addQuad(consumer, matrix, min, max, max, max, max, max, max, max, min, min, max, min, r, g, b, a, 0, 1, 0);
        // North
        addQuad(consumer, matrix, max, max, min, max, min, min, min, min, min, min, max, min, r, g, b, a, 0, 0, -1);
        // South
        addQuad(consumer, matrix, min, max, max, min, min, max, max, min, max, max, max, max, r, g, b, a, 0, 0, 1);
        // West
        addQuad(consumer, matrix, min, max, min, min, min, min, min, min, max, min, max, max, r, g, b, a, -1, 0, 0);
        // East
        addQuad(consumer, matrix, max, max, max, max, min, max, max, min, min, max, max, min, r, g, b, a, 1, 0, 0);

        poseStack.popPose();
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f matrix,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                float r, float g, float b, float a,
                                float nx, float ny, float nz) {
        consumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x4, y4, z4).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }

    private static void renderBlockOutline(PoseStack poseStack, VertexConsumer consumer,
                                           BlockPos pos, Vec3 cameraPos,
                                           float r, float g, float b, float a) {
        poseStack.pushPose();

        double x = pos.getX() - cameraPos.x;
        double y = pos.getY() - cameraPos.y;
        double z = pos.getZ() - cameraPos.z;

        poseStack.translate(x, y, z);

        Matrix4f matrix = poseStack.last().pose();

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
}