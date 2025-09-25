package me.twheatking.enerjolt.networking.packet;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.recipe.SetCurrentRecipeIdPacketUpdate;
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

public record SetCurrentRecipeIdC2SPacket(BlockPos pos, ResourceLocation recipeId) implements CustomPacketPayload {
    public static final Type<SetCurrentRecipeIdC2SPacket> ID =
            new Type<>(EJOLTAPI.id("set_current_recipe_id"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetCurrentRecipeIdC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetCurrentRecipeIdC2SPacket::write, SetCurrentRecipeIdC2SPacket::new);

    public SetCurrentRecipeIdC2SPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readBoolean()?buffer.readResourceLocation():null);
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
         if(recipeId == null) {
             buffer.writeBoolean(false);
         }else {
             buffer.writeBoolean(true);
             buffer.writeResourceLocation(recipeId);
         }
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SetCurrentRecipeIdC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof SetCurrentRecipeIdPacketUpdate setCurrentRecipeIdPacketUpdate))
                return;

            setCurrentRecipeIdPacketUpdate.setRecipeId(data.recipeId);
        });
    }
}
