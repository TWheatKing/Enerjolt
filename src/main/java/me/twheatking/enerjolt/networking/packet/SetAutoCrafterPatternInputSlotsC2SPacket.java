package me.twheatking.enerjolt.networking.packet;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.entity.AutoCrafterBlockEntity;
import me.twheatking.enerjolt.screen.AutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SetAutoCrafterPatternInputSlotsC2SPacket implements CustomPacketPayload {
    public static final Type<SetAutoCrafterPatternInputSlotsC2SPacket> ID =
            new Type<>(EJOLTAPI.id("set_auto_crafter_pattern_input_slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetAutoCrafterPatternInputSlotsC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetAutoCrafterPatternInputSlotsC2SPacket::write, SetAutoCrafterPatternInputSlotsC2SPacket::new);

    private final BlockPos pos;
    private final List<ItemStack> itemStacks;
    private final ResourceLocation recipeId;

    public SetAutoCrafterPatternInputSlotsC2SPacket(BlockPos pos, List<ItemStack> itemStacks, ResourceLocation recipeId) {
        this.pos = pos;

        this.itemStacks = new ArrayList<>(itemStacks);

        while(this.itemStacks.size() < 9)
            this.itemStacks.add(ItemStack.EMPTY);

        this.recipeId = recipeId;
    }

    public SetAutoCrafterPatternInputSlotsC2SPacket(RegistryFriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));

        recipeId = buffer.readResourceLocation();
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        for(ItemStack itemStack:itemStacks)
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, itemStack);

        buffer.writeResourceLocation(recipeId);
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SetAutoCrafterPatternInputSlotsC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = player.containerMenu;

            if(!(menu instanceof AutoCrafterMenu autoCrafterMenu))
                return;

            for(int i = 0;i < data.itemStacks.size();i++)
                autoCrafterMenu.getPatternSlots().setItem(i, data.itemStacks.get(i));

            autoCrafterBlockEntity.setRecipeIdForSetRecipe(data.recipeId);

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });
    }
}
