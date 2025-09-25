package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.inventory.InputOutputItemHandler;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.recipe.ContainerRecipeInputWrapper;
import me.twheatking.enerjolt.recipe.CrystalGrowthChamberRecipe;
import me.twheatking.enerjolt.recipe.EnerjoltRecipes;
import me.twheatking.enerjolt.screen.CrystalGrowthChamberMenu;
import me.twheatking.enerjolt.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalGrowthChamberBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CrystalGrowthChamberRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public CrystalGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.CRYSTAL_GROWTH_CHAMBER_ENTITY.get(), blockPos, blockState,

                "crystal_growth_chamber", CrystalGrowthChamberMenu::new,

                2, EnerjoltRecipes.CRYSTAL_GROWTH_CHAMBER_TYPE.get(), 1,

                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_CAPACITY.getValue(),
                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
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
                    case 0 -> level == null || level.getRecipeManager().
                            getAllRecipesFor(CrystalGrowthChamberRecipe.Type.INSTANCE).stream().
                            map(RecipeHolder::value).map(CrystalGrowthChamberRecipe::getInput).
                            anyMatch(ingredient -> ingredient.test(stack));
                    case 1 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }
        };
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
    protected double getRecipeDependentRecipeDuration(RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        itemHandler.extractItem(0, recipe.value().getInputCount(), false);

        ItemStack output = recipe.value().generateOutput(level.random);

        if(!output.isEmpty())
            itemHandler.setStackInSlot(1, output.copyWithCount(
                    itemHandler.getStackInSlot(1).getCount() + output.getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getMaxOutputCount());
    }
}