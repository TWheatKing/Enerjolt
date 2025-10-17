package me.twheatking.enerjolt.loot;

import com.mojang.serialization.MapCodec;
import me.twheatking.enerjolt.api.EJOLTAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers global loot modifiers for Enerjolt.
 * This allows us to inject armor into vanilla chest loot tables.
 */
public class EnerjoltLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EJOLTAPI.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<EnerjoltArmorLootModifier>>
            ARMOR_LOOT = LOOT_MODIFIERS.register("armor_loot", () -> EnerjoltArmorLootModifier.CODEC);

    public static void register(IEventBus modEventBus) {
        LOOT_MODIFIERS.register(modEventBus);
    }
}