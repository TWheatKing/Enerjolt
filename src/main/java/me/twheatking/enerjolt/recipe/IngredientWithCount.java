package me.twheatking.enerjolt.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public record IngredientWithCount(Ingredient input, int count) {
    public static final IngredientWithCount EMPTY = new IngredientWithCount(Ingredient.EMPTY, 0);

    public IngredientWithCount(Ingredient input) {
        this(input, 1);
    }

    public boolean isEmpty() {
        return input.isEmpty() || count <= 0;
    }

    public static final Codec<IngredientWithCount> CODEC_NONEMPTY = RecordCodecBuilder.create((instance) -> {
        return instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter((input) -> {
            return input.input;
        }), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter((input) -> {
            return input.count;
        })).apply(instance, IngredientWithCount::new);
    });

    public static final Codec<IngredientWithCount> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Ingredient.CODEC.fieldOf("input").forGetter((input) -> {
            return input.input;
        }), Codec.INT.optionalFieldOf("count", 1).forGetter((input) -> {
            return input.count;
        })).apply(instance, IngredientWithCount::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> OPTIONAL_STREAM_CODEC = new StreamCodec<>() {
        @Override
        @NotNull
        public IngredientWithCount decode(@NotNull RegistryFriendlyByteBuf buffer) {
            int count = buffer.readInt();
            if(count <= 0)
                return IngredientWithCount.EMPTY;

            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new IngredientWithCount(input, count);
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, IngredientWithCount ingredient) {
            if(ingredient.isEmpty()) {
                buffer.writeInt(0);
            }else {
                buffer.writeInt(ingredient.count);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient.input);
            }
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithCount> STREAM_CODEC = new StreamCodec<>() {
        @Override
        @NotNull
        public IngredientWithCount decode(@NotNull RegistryFriendlyByteBuf buffer) {
            IngredientWithCount ingredient = IngredientWithCount.OPTIONAL_STREAM_CODEC.decode(buffer);
            if(ingredient.isEmpty())
                throw new DecoderException("Empty IngredientWithCount not allowed");

            return ingredient;
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buffer, IngredientWithCount ingredient) {
            if(ingredient.isEmpty())
                throw new DecoderException("Empty IngredientWithCount not allowed");

            IngredientWithCount.OPTIONAL_STREAM_CODEC.encode(buffer, ingredient);
        }
    };
}
