package me.twheatking.enerjolt.world;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.worldgen.biome.EnerjoltBiomes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModBiomeModifiers {
    private ModBiomeModifiers() {}

    public static final ResourceKey<BiomeModifier> ADD_TIN_ORE_KEY = registerKey("add_tin_ore");

    // Rubber tree biome modifiers
    public static final ResourceKey<BiomeModifier> ADD_RUBBER_TREE_OAK_STYLE_KEY = registerKey("add_rubber_tree_oak_style");
    public static final ResourceKey<BiomeModifier> ADD_RUBBER_TREE_BIRCH_STYLE_KEY = registerKey("add_rubber_tree_birch_style");
    public static final ResourceKey<BiomeModifier> ADD_RUBBER_TREE_SPRUCE_STYLE_KEY = registerKey("add_rubber_tree_spruce_style");
    public static final ResourceKey<BiomeModifier> ADD_RUBBER_TREE_FANCY_OAK_STYLE_KEY = registerKey("add_rubber_tree_fancy_oak_style");
    public static final ResourceKey<BiomeModifier> ADD_RUBBER_TREE_DARK_OAK_STYLE_KEY = registerKey("add_rubber_tree_dark_oak_style");

    // Plagueland biome spawning - handled by TerraBlender in EnerjoltOverworldRegion

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        // Tin ore
        context.register(ADD_TIN_ORE_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TIN_ORE_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        // Oak-style rubber trees in forest and plains biomes
        context.register(ADD_RUBBER_TREE_OAK_STYLE_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.FOREST),
                        biomes.getOrThrow(Biomes.PLAINS),
                        biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS),
                        biomes.getOrThrow(Biomes.MEADOW)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.RUBBER_TREE_OAK_STYLE_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Birch-style rubber trees in birch forests
        context.register(ADD_RUBBER_TREE_BIRCH_STYLE_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.BIRCH_FOREST),
                        biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.RUBBER_TREE_BIRCH_STYLE_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Spruce-style rubber trees in taiga biomes
        context.register(ADD_RUBBER_TREE_SPRUCE_STYLE_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.RUBBER_TREE_SPRUCE_STYLE_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Fancy oak-style rubber trees in flower forests
        context.register(ADD_RUBBER_TREE_FANCY_OAK_STYLE_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.FLOWER_FOREST)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.RUBBER_TREE_FANCY_OAK_STYLE_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // Dark oak-style rubber trees in dark forests
        context.register(ADD_RUBBER_TREE_DARK_OAK_STYLE_KEY, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.DARK_FOREST)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.RUBBER_TREE_DARK_OAK_STYLE_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        // ===== PLAGUELAND BIOME SPAWNING =====
        // Plagueland biome placement is now handled by TerraBlender
        // See EnerjoltOverworldRegion class for bi
    }

    public static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                EJOLTAPI.id(name));
    }
}