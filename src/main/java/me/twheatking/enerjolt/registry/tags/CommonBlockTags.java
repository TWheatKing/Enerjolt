package me.twheatking.enerjolt.registry.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class CommonBlockTags {
    private CommonBlockTags() {}

    public static final TagKey<Block> ORES_TIN = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("c", "ores/tin"));

    public static final TagKey<Block> STORAGE_BLOCKS_SILICON = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/silicon"));
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_TIN = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/raw_tin"));
    public static final TagKey<Block> STORAGE_BLOCKS_TIN = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/tin"));
}