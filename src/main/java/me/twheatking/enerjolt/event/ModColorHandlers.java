package me.twheatking.enerjolt.event;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import net.minecraft.client.renderer.BiomeColors;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * Handles registration of block and item color handlers on the MOD event bus.
 * This enables biome-based foliage coloring for rubber tree leaves.
 */
@EventBusSubscriber(modid = EJOLTAPI.MOD_ID,value = Dist.CLIENT)
public class ModColorHandlers {

    /**
     * Registers block color handlers for rubber tree leaves.
     * This makes the leaves change color based on the biome they're in,
     * just like vanilla oak/birch/dark oak leaves.
     */
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Register all rubber tree leaves to use biome-based foliage coloring
        event.register(
                (state, level, pos, tintIndex) -> {
                    // If we're in-world, get the biome's foliage color
                    if (level != null && pos != null) {
                        return BiomeColors.getAverageFoliageColor(level, pos);
                    }
                    // Default foliage color for inventory/item form
                    return 0x48B518; // Default Minecraft foliage green
                },
                // All rubber tree leaf blocks
                EnerjoltBlocks.RUBBER_OAK_LEAVES.get(),
                EnerjoltBlocks.RUBBER_BIRCH_LEAVES.get(),
                EnerjoltBlocks.RUBBER_SPRUCE_LEAVES.get(),
                EnerjoltBlocks.RUBBER_FANCY_OAK_LEAVES.get(),
                EnerjoltBlocks.RUBBER_DARK_OAK_LEAVES.get()
        );
    }

    /**
     * Registers item color handlers for rubber tree leaves.
     * This makes the leaf items in inventory use the same color as blocks.
     */
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Register item colors to match block colors
        event.register(
                (stack, tintIndex) -> {
                    // Use default foliage color for items
                    return 0x48B518; // Default Minecraft foliage green
                },
                // All rubber tree leaf items
                EnerjoltBlocks.RUBBER_OAK_LEAVES.get(),
                EnerjoltBlocks.RUBBER_BIRCH_LEAVES.get(),
                EnerjoltBlocks.RUBBER_SPRUCE_LEAVES.get(),
                EnerjoltBlocks.RUBBER_FANCY_OAK_LEAVES.get(),
                EnerjoltBlocks.RUBBER_DARK_OAK_LEAVES.get()
        );
    }
}