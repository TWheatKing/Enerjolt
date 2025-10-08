package me.twheatking.enerjolt.registry.tags;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class EnerjoltBiomeTags {
    private EnerjoltBiomeTags() {}

    public static final TagKey<Biome> HAS_STRUCTURE_FACTORY_1 = TagKey.create(Registries.BIOME,
            EJOLTAPI.id("has_structure/factory_1"));

    public static final TagKey<Biome> HAS_STRUCTURE_SMALL_SOLAR_FARM = TagKey.create(Registries.BIOME,
            EJOLTAPI.id("has_structure/small_solar_farm"));
    // NEW: Power Station biome tag
    public static final TagKey<Biome> HAS_STRUCTURE_COAL_POWER_STATION = TagKey.create(Registries.BIOME,
            EJOLTAPI.id("has_structure/coal_power_station"));
}