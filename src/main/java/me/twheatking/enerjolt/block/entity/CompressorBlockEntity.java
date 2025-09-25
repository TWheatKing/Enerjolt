package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.inventory.InputOutputItemHandler;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.recipe.CompressorRecipe;
import me.twheatking.enerjolt.recipe.ContainerRecipeInputWrapper;
import me.twheatking.enerjolt.recipe.EnerjoltRecipes;
import me.twheatking.enerjolt.screen.CompressorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class CompressorBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CompressorRecipe> {
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.COMPRESSOR_ENTITY.get(), blockPos, blockState,

                "compressor", CompressorMenu::new,

                2, EnerjoltRecipes.COMPRESSOR_TYPE.get(), ModConfigs.COMMON_COMPRESSOR_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_COMPRESSOR_CAPACITY.getValue(),
                ModConfigs.COMMON_COMPRESSOR_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_COMPRESSOR_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<CompressorRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        itemHandler.extractItem(0, recipe.value().getInputCount(), false);
        itemHandler.setStackInSlot(1, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(1).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }
}