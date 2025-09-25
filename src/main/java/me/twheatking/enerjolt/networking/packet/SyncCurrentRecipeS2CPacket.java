package me.twheatking.enerjolt.networking.packet;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.recipe.CurrentRecipePacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SyncCurrentRecipeS2CPacket<R extends Recipe<?>> implements CustomPacketPayload {
    public static final Type<SyncCurrentRecipeS2CPacket<?>> ID =
            new Type<>(EJOLTAPI.id("sync_current_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCurrentRecipeS2CPacket<?>> STREAM_CODEC =
            StreamCodec.ofMember(SyncCurrentRecipeS2CPacket::write, SyncCurrentRecipeS2CPacket::new);

    private final BlockPos pos;
    private final RecipeSerializer<R> recipeSerializer;
    private final RecipeHolder<R> currentRecipe;

    public SyncCurrentRecipeS2CPacket(BlockPos pos, RecipeSerializer<R> recipeSerializerId,
                                      @Nullable RecipeHolder<R> currentRecipe) {
        this.pos = pos;
        this.recipeSerializer = recipeSerializerId;
        this.currentRecipe = currentRecipe;
    }

    @SuppressWarnings("unchecked")
    public SyncCurrentRecipeS2CPacket(RegistryFriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        ResourceLocation recipeSerializerId = buffer.readResourceLocation();
        recipeSerializer = (RecipeSerializer<R>)BuiltInRegistries.RECIPE_SERIALIZER.get(recipeSerializerId);
        if(recipeSerializer == null)
            throw new IllegalArgumentException("Recipe Serializer \"" + recipeSerializerId + "\" does not exist!");

        currentRecipe = buffer.readBoolean()?new RecipeHolder<>(buffer.readResourceLocation(),
                recipeSerializer.streamCodec().decode(buffer)):null;
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        ResourceLocation recipeSerializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer);
        if(recipeSerializerId == null)
            throw new IllegalArgumentException("The recipe serializer \"" + recipeSerializer.getClass().getCanonicalName() +
                    "\" is not registered!");

        buffer.writeResourceLocation(recipeSerializerId);
        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);

            buffer.writeResourceLocation(currentRecipe.id());
            recipeSerializer.streamCodec().encode(buffer, currentRecipe.value());
        }
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    @SuppressWarnings("unchecked")
    public static <R extends Recipe<?>> void handle(SyncCurrentRecipeS2CPacket<R> data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof CurrentRecipePacketUpdate) {
                CurrentRecipePacketUpdate<R> currentRecipePacketUpdate = (CurrentRecipePacketUpdate<R>)blockEntity;
                currentRecipePacketUpdate.setCurrentRecipe(data.currentRecipe);
            }
        });
    }
}
