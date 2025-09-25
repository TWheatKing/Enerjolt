package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.paintings.EnerjoltPaintingVariants;
import me.twheatking.enerjolt.world.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModRegistriesProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().
            add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap).
            add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap).
            add(Registries.TEMPLATE_POOL, ModTemplatePools::bootstrap).
            add(Registries.STRUCTURE, ModStructures::bootstrap).
            add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap).
            add(Registries.PAINTING_VARIANT, EnerjoltPaintingVariants::bootstrap).
            add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

    public ModRegistriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, BUILDER, Set.of(EJOLTAPI.MOD_ID));
    }
}
