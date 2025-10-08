package me.twheatking.enerjolt.world;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.OptionalInt;

public final class ModConfiguredFeatures {
    private ModConfiguredFeatures() {}

    public static final ResourceKey<ConfiguredFeature<?, ?>> TIN_ORE_KEY = registerKey("tin_ore");

    // Rubber tree variants - each mimics a vanilla tree style
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE_OAK_STYLE_KEY = registerKey("rubber_tree_oak_style");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE_BIRCH_STYLE_KEY = registerKey("rubber_tree_birch_style");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE_SPRUCE_STYLE_KEY = registerKey("rubber_tree_spruce_style");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE_MEGA_SPRUCE_STYLE_KEY = registerKey("rubber_tree_mega_spruce_style");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE_FANCY_OAK_STYLE_KEY = registerKey("rubber_tree_fancy_oak_style");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE_DARK_OAK_STYLE_KEY = registerKey("rubber_tree_dark_oak_style");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // Existing ore configuration
        RuleTest STONE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        register(context, TIN_ORE_KEY, Feature.ORE, new OreConfiguration(List.of(
                OreConfiguration.target(STONE_ORE_REPLACEABLES, EnerjoltBlocks.TIN_ORE.get().defaultBlockState()),
                OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, EnerjoltBlocks.DEEPSLATE_TIN_ORE.get().defaultBlockState())
        ), 8));

        // Oak-style rubber tree - uses rubber_oak blocks
        // Added forceDirt() to ensure proper ground placement and prevent leaf decay
        register(context, RUBBER_TREE_OAK_STYLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_OAK_LOG.get().defaultBlockState()),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_OAK_LEAVES.get().defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build());

        // Birch-style rubber tree - uses rubber_birch blocks
        register(context, RUBBER_TREE_BIRCH_STYLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_BIRCH_LOG.get().defaultBlockState()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_BIRCH_LEAVES.get().defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build());

        // Spruce-style rubber tree - uses rubber_spruce blocks
        register(context, RUBBER_TREE_SPRUCE_STYLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_SPRUCE_LOG.get().defaultBlockState()),
                new StraightTrunkPlacer(6, 3, 0),
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_SPRUCE_LEAVES.get().defaultBlockState()),
                new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)),
                new TwoLayersFeatureSize(2, 0, 2)
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build());

        // Mega Spruce-style rubber tree - uses rubber_spruce blocks
        register(context, RUBBER_TREE_MEGA_SPRUCE_STYLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_SPRUCE_LOG.get().defaultBlockState()),
                new StraightTrunkPlacer(13, 5, 3),
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_SPRUCE_LEAVES.get().defaultBlockState()),
                new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(3, 4)),
                new TwoLayersFeatureSize(2, 0, 2)
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build());

        // Fancy oak-style rubber tree - uses rubber_fancy_oak blocks
        register(context, RUBBER_TREE_FANCY_OAK_STYLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_FANCY_OAK_LOG.get().defaultBlockState()),
                new FancyTrunkPlacer(3, 11, 0),
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_FANCY_OAK_LEAVES.get().defaultBlockState()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build());

        // Dark oak-style rubber tree - uses rubber_dark_oak blocks
        register(context, RUBBER_TREE_DARK_OAK_STYLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_DARK_OAK_LOG.get().defaultBlockState()),
                new DarkOakTrunkPlacer(6, 2, 1),
                BlockStateProvider.simple(EnerjoltBlocks.RUBBER_DARK_OAK_LEAVES.get().defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        ).dirt(BlockStateProvider.simple(Blocks.DIRT)).ignoreVines().build());
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                EJOLTAPI.id(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature, FC featureConfiguration) {
        context.register(key, new ConfiguredFeature<>(feature, featureConfiguration));
    }
}