package me.twheatking.enerjolt.datagen;

import me.twheatking.enerjolt.worldgen.biome.EnerjoltBiomes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Biome data provider for Enerjolt mod.
 * Registers custom biomes to the game's biome registry.
 */
public class ModBiomeProvider {

    /**
     * Bootstrap method called during datagen to register biomes.
     */
    public static void bootstrap(BootstrapContext<Biome> context) {
        // Get the necessary lookups from context
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> worldCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        // Register the Plagueland biome with proper lookups
        context.register(EnerjoltBiomes.PLAGUELAND, EnerjoltBiomes.plagueland(placedFeatures, worldCarvers));
    }
}