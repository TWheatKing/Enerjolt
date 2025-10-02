package me.twheatking.enerjolt.world;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public final class ModPlacedFeatures {
    private ModPlacedFeatures() {}

    public static final ResourceKey<PlacedFeature> TIN_ORE_KEY = registerKey("tin_ore");

    // Rubber tree placed features - one for each style
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_OAK_STYLE_KEY = registerKey("rubber_tree_oak_style_placed");
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_BIRCH_STYLE_KEY = registerKey("rubber_tree_birch_style_placed");
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_SPRUCE_STYLE_KEY = registerKey("rubber_tree_spruce_style_placed");
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_FANCY_OAK_STYLE_KEY = registerKey("rubber_tree_fancy_oak_style_placed");
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_DARK_OAK_STYLE_KEY = registerKey("rubber_tree_dark_oak_style_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // Existing ore placement
        register(context, TIN_ORE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.TIN_ORE_KEY),
                ModOrePlacement.orePlacement(16, HeightRangePlacement.triangle(
                        VerticalAnchor.absolute(25), VerticalAnchor.absolute(80))));

        // Tree placements - rare spawns to blend naturally
        // Oak-style: 4% chance per chunk
        register(context, RUBBER_TREE_OAK_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_OAK_STYLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.04f, 1)));

        // Birch-style: 4% chance per chunk
        register(context, RUBBER_TREE_BIRCH_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_BIRCH_STYLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.04f, 1)));

        // Spruce-style: 2% chance per chunk
        register(context, RUBBER_TREE_SPRUCE_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_SPRUCE_STYLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.02f, 1)));

        // Fancy oak-style: 2% chance per chunk (rarer since it's larger)
        register(context, RUBBER_TREE_FANCY_OAK_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_FANCY_OAK_STYLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.02f, 1)));

        // Dark oak-style: 2% chance per chunk (rarer since it's 2x2)
        register(context, RUBBER_TREE_DARK_OAK_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_DARK_OAK_STYLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.02f, 1)));
    }

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                EJOLTAPI.id(name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}