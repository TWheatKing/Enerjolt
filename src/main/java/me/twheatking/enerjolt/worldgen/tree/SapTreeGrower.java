package me.twheatking.enerjolt.worldgen.tree;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.world.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class SapTreeGrower {
    public static final TreeGrower RUBBER_TREE_OAK = new TreeGrower(
            EJOLTAPI.MOD_ID + ":rubber_tree_oak",
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.RUBBER_TREE_OAK_STYLE_KEY),
            Optional.empty()
    );

    public static final TreeGrower RUBBER_TREE_BIRCH = new TreeGrower(
            EJOLTAPI.MOD_ID + ":rubber_tree_birch",
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.RUBBER_TREE_BIRCH_STYLE_KEY),
            Optional.empty()
    );

    public static final TreeGrower RUBBER_TREE_SPRUCE = new TreeGrower(
            EJOLTAPI.MOD_ID + ":rubber_tree_spruce",
            Optional.of(ModConfiguredFeatures.RUBBER_TREE_MEGA_SPRUCE_STYLE_KEY),
            Optional.of(ModConfiguredFeatures.RUBBER_TREE_SPRUCE_STYLE_KEY),
            Optional.empty()
    );

    public static final TreeGrower RUBBER_TREE_FANCY_OAK = new TreeGrower(
            EJOLTAPI.MOD_ID + ":rubber_tree_fancy_oak",
            Optional.empty(),
            Optional.of(ModConfiguredFeatures.RUBBER_TREE_FANCY_OAK_STYLE_KEY),
            Optional.empty()
    );

    public static final TreeGrower RUBBER_TREE_DARK_OAK = new TreeGrower(
            EJOLTAPI.MOD_ID + ":rubber_tree_dark_oak",
            Optional.of(ModConfiguredFeatures.RUBBER_TREE_DARK_OAK_STYLE_KEY), // 2x2 mega variant
            Optional.of(ModConfiguredFeatures.RUBBER_TREE_DARK_OAK_STYLE_KEY), // Normal variant
            Optional.empty()
    );
}