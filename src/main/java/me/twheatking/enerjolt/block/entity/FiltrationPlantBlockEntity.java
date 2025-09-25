package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.entity.base.FluidStorageMultiTankMethods;
import me.twheatking.enerjolt.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.fluid.EnerjoltFluids;
import me.twheatking.enerjolt.fluid.EnerjoltFluidStorage;
import me.twheatking.enerjolt.inventory.InputOutputItemHandler;
import me.twheatking.enerjolt.item.EnerjoltItems;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.recipe.EnerjoltRecipes;
import me.twheatking.enerjolt.recipe.FiltrationPlantRecipe;
import me.twheatking.enerjolt.screen.FiltrationPlantMenu;
import me.twheatking.enerjolt.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FiltrationPlantBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<EnerjoltFluidStorage, RecipeInput, FiltrationPlantRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_FILTRATION_PLANT_TANK_CAPACITY.getValue();
    public static final int DIRTY_WATER_CONSUMPTION_PER_RECIPE = ModConfigs.COMMON_FILTRATION_PLANT_DIRTY_WATER_USAGE_PER_RECIPE.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2 || i == 3);

    public FiltrationPlantBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.FILTRATION_PLANT_ENTITY.get(), blockPos, blockState,

                "filtration_plant", FiltrationPlantMenu::new,

                4,
                EnerjoltRecipes.FILTRATION_PLANT_TYPE.get(),
                EnerjoltRecipes.FILTRATION_PLANT_SERIALIZER.get(),
                ModConfigs.COMMON_FILTRATION_PLANT_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_FILTRATION_PLANT_CAPACITY.getValue(),
                ModConfigs.COMMON_FILTRATION_PLANT_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FILTRATION_PLANT_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageMultiTankMethods.INSTANCE,
                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0, 1 -> stack.is(EnerjoltItems.CHARCOAL_FILTER.get());
                    case 2, 3 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    @Override
    protected EnerjoltFluidStorage initFluidStorage() {
        return new EnerjoltFluidStorage(new int[] {
                baseTankCapacity, baseTankCapacity
        }) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                if(!super.isFluidValid(tank, stack))
                    return false;

                return switch(tank) {
                    case 0 -> FluidStack.isSameFluid(stack, new FluidStack(EnerjoltFluids.DIRTY_WATER.get(), 1));
                    case 1 -> FluidStack.isSameFluid(stack, new FluidStack(Fluids.WATER, 1));
                    default -> false;
                };
            }
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void craftItem(RecipeHolder<FiltrationPlantRecipe> recipe) {
        if(level == null || !hasRecipe() || !(level instanceof ServerLevel serverLevel))
            return;

        fluidStorage.drain(new FluidStack(EnerjoltFluids.DIRTY_WATER.get(), DIRTY_WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);
        fluidStorage.fill(new FluidStack(Fluids.WATER, DIRTY_WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);

        for(int i = 0;i < 2;i++) {
            ItemStack charcoalFilter = itemHandler.getStackInSlot(i).copy();
            if(charcoalFilter.isEmpty() && !charcoalFilter.is(EnerjoltItems.CHARCOAL_FILTER.get()))
                continue;

            charcoalFilter.hurtAndBreak(1, serverLevel, null, item -> charcoalFilter.setCount(0));
            itemHandler.setStackInSlot(i, charcoalFilter);
        }

        ItemStack[] outputs = recipe.value().generateOutputs(level.random);

        if(!outputs[0].isEmpty())
            itemHandler.setStackInSlot(2, outputs[0].
                    copyWithCount(itemHandler.getStackInSlot(2).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(3, outputs[1].
                    copyWithCount(itemHandler.getStackInSlot(3).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FiltrationPlantRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return level != null &&
                fluidStorage.getFluid(0).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.getCapacity(1) - fluidStorage.getFluid(1).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                itemHandler.getStackInSlot(0).is(EnerjoltItems.CHARCOAL_FILTER.get()) &&
                itemHandler.getStackInSlot(1).is(EnerjoltItems.CHARCOAL_FILTER.get()) &&
                (maxOutputs[0].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[0])) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[1]));
    }
}