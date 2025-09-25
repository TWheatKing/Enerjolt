package me.twheatking.enerjolt.networking.packet;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.entity.TimeControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SetTimeFromTimeControllerC2SPacket(BlockPos pos, int time) implements CustomPacketPayload {
    public static final Type<SetTimeFromTimeControllerC2SPacket> ID =
            new Type<>(EJOLTAPI.id("set_time_from_time_controller"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetTimeFromTimeControllerC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetTimeFromTimeControllerC2SPacket::write, SetTimeFromTimeControllerC2SPacket::new);

    public SetTimeFromTimeControllerC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(time);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }


    public static void handle(SetTimeFromTimeControllerC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof TimeControllerBlockEntity timeControllerBlockEntity))
                return;

            IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, data.pos,
                    level.getBlockState(data.pos), timeControllerBlockEntity, null);
            if(energyStorage == null)
                return;

            if(energyStorage.getEnergyStored() < TimeControllerBlockEntity.CAPACITY)
                return;

            timeControllerBlockEntity.clearEnergy();

            if(data.time < 0 || data.time > 24000)
                return;

            long currentTime = level.getDayTime();

            int currentDayTime = (int)(currentTime % 24000);

            if(currentDayTime <= data.time)
                level.setDayTime(currentTime - currentDayTime + data.time);
            else
                level.setDayTime(currentTime + 24000 - currentDayTime + data.time);
        });
    }
}
