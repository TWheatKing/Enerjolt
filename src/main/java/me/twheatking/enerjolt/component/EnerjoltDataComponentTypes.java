package me.twheatking.enerjolt.component;

import com.mojang.serialization.Codec;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.item.armor.ArmorAttributeRoll;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class EnerjoltDataComponentTypes {
    private EnerjoltDataComponentTypes() {}

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EJOLTAPI.MOD_ID);

    public static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> registerDataComponentType(
            String name, Supplier<UnaryOperator<DataComponentType.Builder<T>>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.get().apply(DataComponentType.builder()).build());
    }

    // Fix: Change register to registerDataComponentType and add supplier wrapper
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> COORDINATES =
            registerDataComponentType("coordinates", () -> builder ->
                    builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
            registerDataComponentType("energy", () -> builder ->
                    builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ACTIVE =
            registerDataComponentType("active", () -> builder ->
                    builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> WORKING =
            registerDataComponentType("working", () -> builder ->
                    builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PROGRESS =
            registerDataComponentType("progress", () -> builder ->
                    builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_PROGRESS =
            registerDataComponentType("max_progress", () -> builder ->
                    builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_PRODUCTION_LEFT =
            registerDataComponentType("energy_production_left", () -> builder ->
                    builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CurrentItemStackComponent>> CURRENT_ITEM =
            registerDataComponentType("current_item", () -> builder ->
                    builder.persistent(CurrentItemStackComponent.CODEC).networkSynchronized(CurrentItemStackComponent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<InventoryComponent>> INVENTORY =
            registerDataComponentType("inventory", () -> builder ->
                    builder.persistent(InventoryComponent.CODEC).networkSynchronized(InventoryComponent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Direction>> CURRENT_FACE =
            registerDataComponentType("current_face", () -> builder ->
                    builder.persistent(Direction.CODEC).networkSynchronized(Direction.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ACTION_COOLDOWN =
            registerDataComponentType("action_cooldown", () -> builder ->
                    builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DimensionalPositionComponent>> DIMENSIONAL_POSITION =
            registerDataComponentType("dimensional_position", () -> builder ->
                    builder.persistent(DimensionalPositionComponent.CODEC).networkSynchronized(DimensionalPositionComponent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ContaminatedComponent>> CONTAMINATED =
            DATA_COMPONENT_TYPES.register("contaminated", () -> DataComponentType.<ContaminatedComponent>builder()
                    .persistent(ContaminatedComponent.CODEC)
                    .networkSynchronized(ContaminatedComponent.STREAM_CODEC)
                    .build());

    /**
     * Stores the armor rarity (Common, Rare, Legendary)
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ARMOR_RARITY =
            registerDataComponentType("armor_rarity", () -> builder ->
                    builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    /**
     * Stores the list of random attribute rolls on the armor
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ArmorAttributeRoll>>> ARMOR_ATTRIBUTES =
            registerDataComponentType("armor_attributes", () -> builder ->
                    builder.persistent(Codec.list(createArmorAttributeRollCodec()))
                            .networkSynchronized(ByteBufCodecs.fromCodec(Codec.list(createArmorAttributeRollCodec()))));

    /**
     * Stores the current energy in armor pieces (separate from regular ENERGY component)
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ARMOR_ENERGY =
            registerDataComponentType("armor_energy", () -> builder ->
                    builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

// Add this helper method at the end of the class:

    /**
     * Creates a codec for ArmorAttributeRoll serialization
     */
    private static Codec<ArmorAttributeRoll> createArmorAttributeRollCodec() {
        return Codec.STRING.comapFlatMap(
                // Deserialize: "ATTRIBUTE_NAME:value"
                str -> {
                    try {
                        String[] parts = str.split(":");
                        if (parts.length != 2) {
                            return com.mojang.serialization.DataResult.error(() -> "Invalid format");
                        }
                        me.twheatking.enerjolt.item.armor.ArmorAttribute attribute =
                                me.twheatking.enerjolt.item.armor.ArmorAttribute.valueOf(parts[0]);
                        double value = Double.parseDouble(parts[1]);
                        return com.mojang.serialization.DataResult.success(
                                new ArmorAttributeRoll(attribute, value)
                        );
                    } catch (Exception e) {
                        return com.mojang.serialization.DataResult.error(() -> "Parse error: " + e.getMessage());
                    }
                },
                // Serialize: ArmorAttributeRoll to "ATTRIBUTE_NAME:value"
                roll -> roll.getAttribute().name() + ":" + roll.getValue()
        );
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}