package me.twheatking.enerjolt.networking.packet;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.entity.IndustrialGreenhouseBlockEntity;
import me.twheatking.enerjolt.block.entity.PhotosyntheticChamberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record HologramSyncS2CPacket(BlockPos pos, UUID playerId, boolean show) implements CustomPacketPayload {
    public static final Type<HologramSyncS2CPacket> ID =
            new Type<>(EJOLTAPI.id("hologram_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HologramSyncS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(HologramSyncS2CPacket::write, HologramSyncS2CPacket::new);

    public HologramSyncS2CPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readUUID(), buffer.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeUUID(playerId);
        buffer.writeBoolean(show);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(HologramSyncS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            if (blockEntity instanceof IndustrialGreenhouseBlockEntity greenhouse) {
                greenhouse.setHologramStateClient(data.playerId, data.show);
            } else if (blockEntity instanceof PhotosyntheticChamberBlockEntity chamber) {
                chamber.setHologramStateClient(data.playerId, data.show);
            }
        });
    }
}