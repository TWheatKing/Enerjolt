package me.twheatking.enerjolt.networking.packet;

import io.netty.buffer.ByteBuf;
import me.twheatking.enerjolt.block.entity.IndustrialGreenhouseBlockEntity;
import me.twheatking.enerjolt.block.entity.PhotosyntheticChamberBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import me.twheatking.enerjolt.api.EJOLTAPI;

import java.util.UUID;

public record HologramSyncS2CPacket(BlockPos pos, UUID playerId, boolean show) implements CustomPacketPayload {

    public static final Type<HologramSyncS2CPacket> ID = new Type<>(
            ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "hologram_sync")
    );

    // Custom UUID StreamCodec
    public static final StreamCodec<ByteBuf, UUID> UUID_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public UUID decode(ByteBuf buf) {
            return new UUID(buf.readLong(), buf.readLong());
        }

        @Override
        public void encode(ByteBuf buf, UUID uuid) {
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
        }
    };

    public static final StreamCodec<ByteBuf, HologramSyncS2CPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            HologramSyncS2CPacket::pos,
            UUID_STREAM_CODEC,
            HologramSyncS2CPacket::playerId,
            ByteBufCodecs.BOOL,
            HologramSyncS2CPacket::show,
            HologramSyncS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(HologramSyncS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Level level = mc.level;

            if (level == null) return;

            BlockEntity be = level.getBlockEntity(packet.pos);

            if (be instanceof IndustrialGreenhouseBlockEntity greenhouse) {
                greenhouse.setHologramStateClient(packet.playerId, packet.show);
            } else if (be instanceof PhotosyntheticChamberBlockEntity chamber) {
                chamber.setHologramStateClient(packet.playerId, packet.show);
            }
        });
    }
}