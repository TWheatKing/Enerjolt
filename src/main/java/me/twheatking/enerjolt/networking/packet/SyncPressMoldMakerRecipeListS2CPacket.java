package me.twheatking.enerjolt.networking.packet;

import com.mojang.datafixers.util.Pair;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.entity.PressMoldMakerBlockEntity;
import me.twheatking.enerjolt.recipe.PressMoldMakerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SyncPressMoldMakerRecipeListS2CPacket implements CustomPacketPayload {
    public static final Type<SyncPressMoldMakerRecipeListS2CPacket> ID =
            new Type<>(EJOLTAPI.id("sync_press_mold_maker_recipe_list"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPressMoldMakerRecipeListS2CPacket> STREAM_CODEC =
            StreamCodec.ofMember(SyncPressMoldMakerRecipeListS2CPacket::write, SyncPressMoldMakerRecipeListS2CPacket::new);

    private final BlockPos pos;
    private final List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList;

    public SyncPressMoldMakerRecipeListS2CPacket(BlockPos pos, List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList) {
        this.pos = pos;
        this.recipeList = recipeList;
    }

    public SyncPressMoldMakerRecipeListS2CPacket(RegistryFriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        int size = buffer.readInt();
        recipeList = IntStream.range(0, size).mapToObj(i -> Pair.of(new RecipeHolder<>(buffer.readResourceLocation(),
                PressMoldMakerRecipe.Serializer.INSTANCE.streamCodec().decode(buffer)), buffer.readBoolean())).
                collect(Collectors.toList());
    }

     public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        buffer.writeInt(recipeList.size());
        recipeList.forEach(entry -> {
            buffer.writeResourceLocation(entry.getFirst().id());
            PressMoldMakerRecipe.Serializer.INSTANCE.streamCodec().encode(buffer, entry.getFirst().value());
            buffer.writeBoolean(entry.getSecond());
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SyncPressMoldMakerRecipeListS2CPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.player().level().getBlockEntity(data.pos);

            //BlockEntity
            if(blockEntity instanceof PressMoldMakerBlockEntity pressMoldMakerBlockEntity) {
                pressMoldMakerBlockEntity.setRecipeList(data.recipeList);
            }
        });
    }
}
