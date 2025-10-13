package me.twheatking.enerjolt.compat.create;

import me.twheatking.enerjolt.Enerjolt;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.EnerjoltBlockEntities;
import me.twheatking.enerjolt.item.EnerjoltItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

/**
 * Simplified Create compatibility for Create 6.0.6
 * Just registers the blocks/items/block entities without advanced stress system integration
 */
public class CreateCompat {

    // Block registration
    public static final DeferredBlock<CreateKineticAdapterBlock> KINETIC_ADAPTER_BLOCK =
            EnerjoltBlocks.BLOCKS.register("kinetic_adapter",
                    () -> new CreateKineticAdapterBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f)
                            .requiresCorrectToolForDrops()));

    // Item registration
    public static final DeferredItem<Item> KINETIC_ADAPTER_ITEM =
            EnerjoltItems.ITEMS.register("kinetic_adapter",
                    () -> new BlockItem(KINETIC_ADAPTER_BLOCK.get(), new Item.Properties()));

    // Block entity registration - Create BlockEntities need the type as first param
    public static Supplier<BlockEntityType<CreateKineticAdapterBlockEntity>> KINETIC_ADAPTER_BLOCK_ENTITY;

    static {
        // Register with a lambda that provides all three parameters
        KINETIC_ADAPTER_BLOCK_ENTITY = EnerjoltBlockEntities.BLOCK_ENTITIES.register("kinetic_adapter",
                () -> {
                    BlockEntityType<CreateKineticAdapterBlockEntity> type = BlockEntityType.Builder.of(
                            (pos, state) -> new CreateKineticAdapterBlockEntity(null, pos, state),
                            KINETIC_ADAPTER_BLOCK.get()
                    ).build(null);
                    return type;
                });
    }

    public static void init() {
        Enerjolt.LOGGER.info("Create mod detected. Enerjolt compatibility initialized.");
        // Note: Advanced stress system integration removed for compatibility
        // The adapter will still work for basic rotation transfer
    }
}