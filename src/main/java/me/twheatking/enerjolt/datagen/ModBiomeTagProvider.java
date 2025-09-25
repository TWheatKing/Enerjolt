package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.registry.tags.EnerjoltBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends BiomeTagsProvider {
    public ModBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EJOLTAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(EnerjoltBiomeTags.HAS_STRUCTURE_FACTORY_1).
                add(Biomes.FOREST,
                        Biomes.FLOWER_FOREST);

        tag(EnerjoltBiomeTags.HAS_STRUCTURE_SMALL_SOLAR_FARM).
                add(Biomes.DESERT,
                        Biomes.BADLANDS,
                        Biomes.SAVANNA);
    }
}
