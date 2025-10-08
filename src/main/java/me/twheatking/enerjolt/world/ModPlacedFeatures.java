package me.twheatking.enerjolt.world;

import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

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

        // Tree placements - RARE spawns with proper ground placement
        // Oak-style: 1 in 200 chunks (0.5% chance)
        register(context, RUBBER_TREE_OAK_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_OAK_STYLE_KEY),
                rubberTreePlacement(100));

        // Birch-style: 1 in 200 chunks (0.5% chance)
        register(context, RUBBER_TREE_BIRCH_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_BIRCH_STYLE_KEY),
                rubberTreePlacement(100));

        // Spruce-style: 1 in 300 chunks (0.33% chance)
        register(context, RUBBER_TREE_SPRUCE_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_SPRUCE_STYLE_KEY),
                rubberTreePlacement(100));

        // Fancy oak-style: 1 in 300 chunks (0.33% chance)
        register(context, RUBBER_TREE_FANCY_OAK_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_FANCY_OAK_STYLE_KEY),
                rubberTreePlacement(100));

        // Dark oak-style: 1 in 300 chunks (0.33% chance)
        register(context, RUBBER_TREE_DARK_OAK_STYLE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.RUBBER_TREE_DARK_OAK_STYLE_KEY),
                rubberTreePlacement(100));
    }

    /**
     * Custom tree placement that ensures trees spawn on the actual ground surface,
     * not on top of other trees. Uses RarityFilter for proper rare spawning.
     *
     * @param rarity 1 in N chunks will have this tree (e.g., 200 = 0.5% chance per chunk)
     */
    private static List<PlacementModifier> rubberTreePlacement(int rarity) {
        return List.of(
                RarityFilter.onAverageOnceEvery(rarity),  // Makes trees very rare
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,  // This ensures placement on actual ground, not vegetation
                BiomeFilter.biome(),
                BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(
                        net.minecraft.world.level.block.Blocks.OAK_SAPLING.defaultBlockState(),
                        net.minecraft.core.BlockPos.ZERO))
        );
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