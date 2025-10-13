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
 * - Rarely on LAND (continentalness > 0 = not ocean)
 * - In cold areas (matching temperature 0.3F)
 * - With high humidity (matching downfall 0.8F)
 * - On surface, not underground
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
        // Plagueland spawns ONLY next to Dark Forests and Swamps
        // We match their climate parameters closely so it appears adjacent to them

        // Dark Forest parameters: Cool temp, normal-high humidity, inland
        // Variant 1: Right next to Dark Forests (matching their climate)
        addBiome(mapper,
                Climate.parameters(
                        Climate.Parameter.span(0.0F, 0.3F),      // Temperature: COOL (Dark Forest range)
                        Climate.Parameter.span(0.4F, 0.8F),      // Humidity: MODERATE to WET (Dark Forest)
                        Climate.Parameter.span(0.2F, 0.5F),      // Continentalness: INLAND (Dark Forest)
                        Climate.Parameter.span(-0.3F, 0.1F),     // Erosion: Similar to Dark Forest
                        Climate.Parameter.point(0.0F),           // Depth: SURFACE
                        Climate.Parameter.span(-0.2F, 0.2F),     // Weirdness: NORMAL (Dark Forest)
                        0.0F                                      // Offset
                ),
                EnerjoltBiomes.PLAGUELAND
        );

        // Variant 2: Right next to Swamps (matching their climate)
        // Swamp parameters: Warm-ish temp, very high humidity, inland/coastal
        addBiome(mapper,
                Climate.parameters(
                        Climate.Parameter.span(0.3F, 0.6F),      // Temperature: WARM (Swamp range)
                        Climate.Parameter.span(0.8F, 1.0F),      // Humidity: VERY WET (Swamp)
                        Climate.Parameter.span(0.1F, 0.4F),      // Continentalness: COASTAL to INLAND (Swamp)
                        Climate.Parameter.span(-0.2F, 0.3F),     // Erosion: Similar to Swamp
                        Climate.Parameter.point(0.0F),           // Depth: SURFACE
                        Climate.Parameter.span(-0.3F, 0.3F),     // Weirdness: VARIED (Swamp)
                        0.0F                                      // Offset
                ),
                EnerjoltBiomes.PLAGUELAND
        );
    }
}