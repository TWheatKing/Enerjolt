package me.twheatking.enerjolt.fluid;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.item.EnerjoltItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EnerjoltFluids {
    private EnerjoltFluids() {}

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, EJOLTAPI.MOD_ID);

    public static final DeferredHolder<Fluid, FlowingFluid> DIRTY_WATER = FLUIDS.register("dirty_water",
            () -> new BaseFlowingFluid.Source(EnerjoltFluids.DIRTY_WATER_PROPS));
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_DIRTY_WATER = FLUIDS.register("flowing_dirty_water",
            () -> new BaseFlowingFluid.Flowing(EnerjoltFluids.DIRTY_WATER_PROPS));
    public static final DeferredBlock<LiquidBlock> DIRTY_WATER_BLOCK = EnerjoltBlocks.BLOCKS.register("dirty_water",
            () -> new LiquidBlock(DIRTY_WATER.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final DeferredItem<BucketItem> DIRTY_WATER_BUCKET_ITEM = EnerjoltItems.ITEMS.register("dirty_water_bucket",
            () -> new BucketItem(DIRTY_WATER.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    private static final BaseFlowingFluid.Properties DIRTY_WATER_PROPS = new BaseFlowingFluid.Properties(
            EnerjoltFluidTypes.DIRTY_WATER_FLUID_TYPE, DIRTY_WATER, FLOWING_DIRTY_WATER
    ).explosionResistance(100.f).block(DIRTY_WATER_BLOCK).bucket(DIRTY_WATER_BUCKET_ITEM);

    public static void register(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }
}
