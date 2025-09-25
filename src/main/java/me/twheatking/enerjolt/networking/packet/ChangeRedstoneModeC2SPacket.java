package me.twheatking.enerjolt.networking.packet;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.machine.configuration.RedstoneModeUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ChangeRedstoneModeC2SPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ChangeRedstoneModeC2SPacket> ID =
            new Type<>(EJOLTAPI.id("change_redstone_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeRedstoneModeC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(ChangeRedstoneModeC2SPacket::write, ChangeRedstoneModeC2SPacket::new);

    public ChangeRedstoneModeC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(ChangeRedstoneModeC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof RedstoneModeUpdate redstoneModeUpdate))
                return;

            redstoneModeUpdate.setNextRedstoneMode();
        });
    }
}
