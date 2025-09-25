package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.inventory.InputOutputItemHandler;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.recipe.ContainerRecipeInputWrapper;
import me.twheatking.enerjolt.recipe.CrusherRecipe;
import me.twheatking.enerjolt.recipe.EnerjoltRecipes;
import me.twheatking.enerjolt.screen.CrusherMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class CrusherBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CrusherRecipe> {
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CrusherBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.CRUSHER_ENTITY.get(), blockPos, blockState,

                "crusher", CrusherMenu::new,

                2, EnerjoltRecipes.CRUSHER_TYPE.get(), ModConfigs.COMMON_CRUSHER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_CRUSHER_CAPACITY.getValue(),
                ModConfigs.COMMON_CRUSHER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_CRUSHER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
}