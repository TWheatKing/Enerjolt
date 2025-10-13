package me.twheatking.enerjolt.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import me.twheatking.enerjolt.api.EJOLTAPI;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

/**
 * TerraBlender Region for Enerjolt biomes.
 * This controls WHERE in the world our custom biomes spawn.
 *
 * Plagueland spawns:
 * - Rarely (weight 2)
 * - In cold, inland areas
 * - With high humidity
 */
public class EnerjoltRegion extends Region {

    public EnerjoltRegion(int weight) {
        super(
                ResourceLocation.fromNamespaceAndPath(EJOLTAPI.MOD_ID, "overworld"),
                RegionType.OVERWORLD,
                weight
        );
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Add the Plagueland biome with specific climate parameters
        // These parameters control where the biome spawns

        // Climate parameters explanation:
        // - Temperature: COLD (0.0 to -0.5) - cold areas
        // - Humidity: WET (0.3 to 1.0) - high rainfall
        // - Continentalness: MID_INLAND (0.0 to 0.2) - away from coast but not far inland
        // - Erosion: EROSION_4 (-0.2 to 0.2) - medium erosion
        // - Depth: SURFACE (0) - surface level
        // - Weirdness: MID_SLICE_NORMAL_ASCENDING (0) - normal terrain
        // - Offset: 0.0 - no offset

        this.addBiome(mapper,
                Climate.parameters(
                        Climate.Parameter.span(-0.5F, 0.0F),    // Temperature: COLD
                        Climate.Parameter.span(0.3F, 1.0F),      // Humidity: WET
                        Climate.Parameter.span(0.0F, 0.2F),      // Continentalness: MID_INLAND
                        Climate.Parameter.span(-0.2F, 0.2F),     // Erosion: MEDIUM
                        Climate.Parameter.point(0.0F),           // Depth: SURFACE
                        Climate.Parameter.point(0.0F),           // Weirdness: NORMAL
                        0.0F                                      // Offset
                ),
                EnerjoltBiomes.PLAGUELAND
        );

        // TODO: Add more spawn variants for different conditions
        // You can add multiple parameter sets to make the biome spawn in different places
        // Example: also spawn in very cold + very wet areas
        this.addBiome(mapper,
                Climate.parameters(
                        Climate.Parameter.span(-1.0F, -0.5F),    // Temperature: VERY COLD
                        Climate.Parameter.span(0.5F, 1.0F),      // Humidity: VERY WET
                        Climate.Parameter.span(0.1F, 0.3F),      // Continentalness: INLAND
                        Climate.Parameter.span(-0.3F, 0.3F),     // Erosion: VARIED
                        Climate.Parameter.point(0.0F),           // Depth: SURFACE
                        Climate.Parameter.point(0.0F),           // Weirdness: NORMAL
                        0.0F                                      // Offset
                ),
                EnerjoltBiomes.PLAGUELAND
        );
    }
}