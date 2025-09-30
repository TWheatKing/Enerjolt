package me.twheatking.enerjolt.networking.packet;

import io.netty.buffer.ByteBuf;
import me.twheatking.enerjolt.block.entity.IndustrialGreenhouseBlockEntity;
import me.twheatking.enerjolt.block.entity.PhotosyntheticChamberBlockEntity;
import me.twheatking.enerjolt.block.multiblock.MultiblockPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import me.twheatking.enerjolt.api.EJOLTAPI;

import java.util.ArrayList;
import java.util.List;

public record MultiblockPatternSyncS2CPacket(
        BlockPos controllerPos,
        Direction facing,
        int size,
        boolean isValid,
        List<BlockPos> missingBlocks,
        List<BlockPos> glassPositions,
        BlockPos centerPos
) implements CustomPacketPayload {

    public static final Type<MultiblockPatternSyncS2CPacket> ID = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "multiblock_pattern_sync")
    );

    // Manual StreamCodec implementation since we have 7 fields (composite only supports up to 6)
    public static final StreamCodec<ByteBuf, MultiblockPatternSyncS2CPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MultiblockPatternSyncS2CPacket decode(ByteBuf buf) {
            BlockPos controllerPos = BlockPos.STREAM_CODEC.decode(buf);
            Direction facing = Direction.STREAM_CODEC.decode(buf);
            int size = ByteBufCodecs.INT.decode(buf);
            boolean isValid = ByteBufCodecs.BOOL.decode(buf);

            // Decode missing blocks list
            int missingCount = ByteBufCodecs.INT.decode(buf);
            List<BlockPos> missingBlocks = new ArrayList<>(missingCount);
            for (int i = 0; i < missingCount; i++) {
                missingBlocks.add(BlockPos.STREAM_CODEC.decode(buf));
            }

            // Decode glass positions list
            int glassCount = ByteBufCodecs.INT.decode(buf);
            List<BlockPos> glassPositions = new ArrayList<>(glassCount);
            for (int i = 0; i < glassCount; i++) {
                glassPositions.add(BlockPos.STREAM_CODEC.decode(buf));
            }

            BlockPos centerPos = BlockPos.STREAM_CODEC.decode(buf);

            return new MultiblockPatternSyncS2CPacket(controllerPos, facing, size, isValid,
                    missingBlocks, glassPositions, centerPos);
        }

        @Override
        public void encode(ByteBuf buf, MultiblockPatternSyncS2CPacket packet) {
            BlockPos.STREAM_CODEC.encode(buf, packet.controllerPos);
            Direction.STREAM_CODEC.encode(buf, packet.facing);
            ByteBufCodecs.INT.encode(buf, packet.size);
            ByteBufCodecs.BOOL.encode(buf, packet.isValid);

            // Encode missing blocks list
            ByteBufCodecs.INT.encode(buf, packet.missingBlocks.size());
            for (BlockPos pos : packet.missingBlocks) {
                BlockPos.STREAM_CODEC.encode(buf, pos);
            }

            // Encode glass positions list
            ByteBufCodecs.INT.encode(buf, packet.glassPositions.size());
            for (BlockPos pos : packet.glassPositions) {
                BlockPos.STREAM_CODEC.encode(buf, pos);
            }

            BlockPos.STREAM_CODEC.encode(buf, packet.centerPos);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(MultiblockPatternSyncS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Level level = mc.level;

            if (level == null) return;

            BlockEntity be = level.getBlockEntity(packet.controllerPos);

            if (be instanceof IndustrialGreenhouseBlockEntity greenhouse) {
                greenhouse.setPatternClient(packet);
            } else if (be instanceof PhotosyntheticChamberBlockEntity chamber) {
                chamber.setPatternClient(packet);
            }
        });
    }
}