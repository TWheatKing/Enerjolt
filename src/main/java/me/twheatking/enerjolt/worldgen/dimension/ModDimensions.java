package me.twheatking.enerjolt.worldgen.dimension;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.List;
import java.util.OptionalLong;

/**
 * Defines the Contamination Zone dimension - a hostile extraction-based dimension
 * inspired by The Division's Dark Zone gameplay.
 */
public class ModDimensions {

    // ===== DIMENSION KEYS =====
    public static final ResourceKey<LevelStem> CONTAMINATION_ZONE_KEY = ResourceKey.create(
            Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "contamination_zone")
    );

    public static final ResourceKey<Level> CONTAMINATION_ZONE_LEVEL_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "contamination_zone")
    );

    public static final ResourceKey<DimensionType> CONTAMINATION_ZONE_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "contamination_zone")
    );

    // ===== BOOTSTRAP METHODS =====

    /**
     * Registers the custom dimension type with specific properties for the Contamination Zone
     */
    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        context.register(CONTAMINATION_ZONE_TYPE, new DimensionType(
                OptionalLong.of(18000), // Fixed time - perpetual night for danger
                true, // hasSkylight - allows day/night detection
                false, // hasCeiling - no bedrock ceiling
                false, // ultraWarm - water doesn't evaporate
                false, // natural - compasses spin randomly (disorienting!)
                1.0, // coordinateScale - normal distance
                false, // bedWorks - beds explode (can't set spawn)
                false, // respawnAnchorWorks - anchors don't work either
                0, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                BuiltinDimensionTypes.NETHER_EFFECTS, // effects - thick fog like Nether
                0.0f, // ambientLight - full darkness at night
                new DimensionType.MonsterSettings(
                        false, // piglinSafe - piglins get zombified
                        true, // hasRaids - can have raids
                        UniformInt.of(0, 15), // monsterSpawnLightLevel - mobs spawn at ANY light!
                        15 // monsterSpawnBlockLightLimit
                )
        ));
    }

    /**
     * Registers the dimension stem (the actual dimension instance)
     */
    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        // Create a simple biome source with a single biome point
        // Using plains as placeholder - you can replace with custom biome later
        Climate.ParameterList<net.minecraft.core.Holder<Biome>> parameterList =
                new Climate.ParameterList<>(
                        List.of(
                                com.mojang.datafixers.util.Pair.of(
                                        Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                                        biomeRegistry.getOrThrow(Biomes.PLAINS)
                                )
                        )
                );

        // Create multi-noise biome source directly
        BiomeSource biomeSource = MultiNoiseBiomeSource.createFromList(parameterList);

        // Create noise-based chunk generator
        NoiseBasedChunkGenerator chunkGenerator = new NoiseBasedChunkGenerator(
                biomeSource,
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD)
        );

        // Register the dimension
        LevelStem stem = new LevelStem(
                dimTypes.getOrThrow(CONTAMINATION_ZONE_TYPE),
                chunkGenerator
        );

        context.register(CONTAMINATION_ZONE_KEY, stem);
    }
}