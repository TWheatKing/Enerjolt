package me.twheatking.enerjolt.networking.packet;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.entity.PressMoldMakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CraftPressMoldMakerRecipeC2SPacket(BlockPos pos, ResourceLocation resourceLocation) implements CustomPacketPayload {
    public static final Type<CraftPressMoldMakerRecipeC2SPacket> ID =
            new Type<>(EJOLTAPI.id("craft_press_mold_maker_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CraftPressMoldMakerRecipeC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(CraftPressMoldMakerRecipeC2SPacket::write, CraftPressMoldMakerRecipeC2SPacket::new);

    public CraftPressMoldMakerRecipeC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readResourceLocation());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeResourceLocation(resourceLocation);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(CraftPressMoldMakerRecipeC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity))
                return;

            pressMoldMakerBlockEntity.craftItem(data.resourceLocation);
        });
    }
}
