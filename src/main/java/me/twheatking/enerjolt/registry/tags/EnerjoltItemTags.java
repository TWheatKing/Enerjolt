package me.twheatking.enerjolt.registry.tags;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class EnerjoltItemTags {
    private EnerjoltItemTags() {}

    public static final TagKey<Item> RAW_METAL_PRESS_MOLDS = TagKey.create(Registries.ITEM,
            EJOLTAPI.id("metal_press/raw_press_molds"));

    public static final TagKey<Item> METAL_PRESS_MOLDS = TagKey.create(Registries.ITEM,
            EJOLTAPI.id("metal_press/press_molds"));
}