package me.twheatking.enerjolt.worldgen.biome;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Biome registration for Enerjolt mod.
 * Currently includes the Plagueland biome.
 */
public class EnerjoltBiomes {

    // ResourceKey for the Plagueland biome
    public static final ResourceKey<Biome> PLAGUELAND = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "plagueland")
    );

    /**
     * Creates the Plagueland biome with toxic wasteland properties.
     * Features:
     * - Dark, ominous atmosphere
     * - Toxic water (will be customized later)
     * - Hostile environment
     * - Rare spawning
     */
    public static Biome plagueland(HolderGetter<PlacedFeature> placedFeatures, HolderGetter<ConfiguredWorldCarver<?>> worldCarvers) {
        // Mob spawns - hostile and dangerous
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        // Add hostile mob spawns (higher rates for zombies and skeletons)
        spawnBuilder.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 100, 4, 8));
        spawnBuilder.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 80, 4, 6));
        spawnBuilder.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 60, 3, 5));
        spawnBuilder.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 40, 2, 4));

        // Generation settings - START SIMPLE to avoid feature cycles
        // We'll add custom features later via biome modifiers
        BiomeGenerationSettings.Builder generationBuilder = new BiomeGenerationSettings.Builder(
                placedFeatures, worldCarvers
        );

        // Special effects - dark, toxic atmosphere
        BiomeSpecialEffects.Builder effectsBuilder = new BiomeSpecialEffects.Builder()
                // Fog color - sickly green-gray
                .fogColor(0x6B7F6B) // Dark gray-green
                // Water color - toxic green
                .waterColor(0x4A6B4A) // Dark toxic green
                .waterFogColor(0x2F4F2F) // Very dark green
                // Sky color - dark and ominous
                .skyColor(0x5A6B5A) // Dark gray
                // Foliage and grass - dead, brown colors
                .grassColorOverride(0x5C5C3D) // Dead grass brown
                .foliageColorOverride(0x4A4A2F) // Dead foliage
                // Ambient mood sound - eerie
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);

        // Climate settings - cold, wet, hostile
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true) // Rain (will look toxic)
                .temperature(0.3F) // Cold
                .downfall(0.8F) // High rainfall
                .specialEffects(effectsBuilder.build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(generationBuilder.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .build();
    }
}