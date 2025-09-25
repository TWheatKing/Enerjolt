package me.twheatking.enerjolt.world.village;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import me.twheatking.enerjolt.api.EJOLTAPI;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = EJOLTAPI.MOD_ID)
public class VillageAddition {

    private static final Logger LOGGER = LogUtils.getLogger();

    // Reflection fields for accessing private members of StructureTemplatePool
    private static Field templatesField;
    private static Field rawTemplatesField;
    private static boolean reflectionInitialized = false;

    static {
        initializeReflection();
    }

    private static void initializeReflection() {
        try {
            // Try to access the private fields using reflection
            templatesField = StructureTemplatePool.class.getDeclaredField("templates");
            templatesField.setAccessible(true);

            rawTemplatesField = StructureTemplatePool.class.getDeclaredField("rawTemplates");
            rawTemplatesField.setAccessible(true);

            reflectionInitialized = true;
            LOGGER.info("Successfully initialized reflection access to StructureTemplatePool fields");
        } catch (NoSuchFieldException e) {
            LOGGER.error("Failed to access StructureTemplatePool fields via reflection. Village structures will not be added.", e);
            LOGGER.error("This may be due to field name changes in this Minecraft version. Check field mappings.");
            reflectionInitialized = false;
        } catch (SecurityException e) {
            LOGGER.error("Security manager prevented reflection access to StructureTemplatePool fields.", e);
            reflectionInitialized = false;
        }
    }

    @SubscribeEvent
    public static void addNewVillageBuilding(ServerStartingEvent event) {
        if (!reflectionInitialized) {
            LOGGER.warn("Reflection not initialized. Skipping village structure addition.");
            return;
        }

        try {
            var registry = event.getServer().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);

            // Add power stations to different village types
            addBuildingToPool(registry,
                    ResourceLocation.fromNamespaceAndPath("minecraft", "village/plains/houses"),
                    EJOLTAPI.id("village/plains/houses/plains_power_station"), 200);

            addBuildingToPool(registry,
                    ResourceLocation.fromNamespaceAndPath("minecraft", "village/desert/houses"),
                    EJOLTAPI.id("village/desert/houses/desert_power_station"), 200);

            addBuildingToPool(registry,
                    ResourceLocation.fromNamespaceAndPath("minecraft", "village/savanna/houses"),
                    EJOLTAPI.id("village/savanna/houses/savanna_power_station"), 200);

            addBuildingToPool(registry,
                    ResourceLocation.fromNamespaceAndPath("minecraft", "village/snowy/houses"),
                    EJOLTAPI.id("village/snowy/houses/snowy_power_station"), 200);

            addBuildingToPool(registry,
                    ResourceLocation.fromNamespaceAndPath("minecraft", "village/taiga/houses"),
                    EJOLTAPI.id("village/taiga/houses/taiga_power_station"), 200);

            LOGGER.info("Successfully added Enerjolt power stations to village structure pools");
        } catch (Exception e) {
            LOGGER.error("Failed to add village buildings", e);
        }
    }

    private static void addBuildingToPool(net.minecraft.core.Registry<StructureTemplatePool> templatePoolRegistry,
                                          ResourceLocation poolRL, ResourceLocation pieceRL, int weight) {
        try {
            // Get the pool from registry
            Optional<Holder.Reference<StructureTemplatePool>> poolHolder = templatePoolRegistry.getHolder(ResourceKey.create(Registries.TEMPLATE_POOL, poolRL));

            if (poolHolder.isEmpty()) {
                LOGGER.warn("Could not find template pool: {}", poolRL);
                return;
            }

            StructureTemplatePool templatePool = poolHolder.get().value();

            // Create the new structure piece
            StructurePoolElement element = StructurePoolElement.legacy(pieceRL.toString()).apply(StructureTemplatePool.Projection.RIGID);

            // Use reflection to access and modify the private templates list
            @SuppressWarnings("unchecked")
            List<StructurePoolElement> templates = (List<StructurePoolElement>) templatesField.get(templatePool);

            // Add the new element with appropriate weight (add multiple times for weight)
            for (int i = 0; i < weight; i++) {
                templates.add(element);
            }

            // Update the rawTemplates list as well
            @SuppressWarnings("unchecked")
            List<Pair<StructurePoolElement, Integer>> rawTemplates = (List<Pair<StructurePoolElement, Integer>>) rawTemplatesField.get(templatePool);
            List<Pair<StructurePoolElement, Integer>> entries = new ArrayList<>(rawTemplates);
            entries.add(Pair.of(element, weight));
            rawTemplatesField.set(templatePool, entries);

            LOGGER.debug("Successfully added structure {} to pool {} with weight {}", pieceRL, poolRL, weight);

        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to modify StructureTemplatePool via reflection for pool: {}", poolRL, e);
        } catch (ClassCastException e) {
            LOGGER.error("Field type mismatch when accessing StructureTemplatePool fields for pool: {}", poolRL, e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error when adding building to pool: {}", poolRL, e);
        }
    }

    /**
     * Alternative approach using a more defensive field access pattern
     */
    @SuppressWarnings("unchecked")
    private static <T> T getFieldValue(Object object, Field field, Class<T> expectedType) throws IllegalAccessException {
        Object value = field.get(object);
        if (!expectedType.isInstance(value)) {
            throw new ClassCastException("Expected " + expectedType.getSimpleName() + " but got " + value.getClass().getSimpleName());
        }
        return (T) value;
    }
}