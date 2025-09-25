package me.twheatking.enerjolt.block.entity;

import me.twheatking.enerjolt.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.twheatking.enerjolt.config.ModConfigs;
import me.twheatking.enerjolt.inventory.InputOutputItemHandler;
import me.twheatking.enerjolt.machine.upgrade.UpgradeModuleModifier;
import me.twheatking.enerjolt.recipe.ContainerRecipeInputWrapper;
import me.twheatking.enerjolt.recipe.EnerjoltRecipes;
import me.twheatking.enerjolt.recipe.MetalPressRecipe;
import me.twheatking.enerjolt.registry.tags.EnerjoltItemTags;
import me.twheatking.enerjolt.screen.MetalPressMenu;
import me.twheatking.enerjolt.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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

public class MetalPressBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, MetalPressRecipe> {
    private final IItemHandler itemHandlerTopSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 1);
    private final IItemHandler itemHandlerOthersSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 2);

    public MetalPressBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EnerjoltBlockEntities.METAL_PRESS_ENTITY.get(), blockPos, blockState,

                "metal_press", MetalPressMenu::new,

                3, EnerjoltRecipes.METAL_PRESS_TYPE.get(), ModConfigs.COMMON_METAL_PRESS_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_METAL_PRESS_CAPACITY.getValue(),
                ModConfigs.COMMON_METAL_PRESS_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_METAL_PRESS_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
                    case 0 -> level == null || level.getRecipeManager().getAllRecipesFor(MetalPressRecipe.Type.INSTANCE).stream().
                            map(RecipeHolder::value).map(MetalPressRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
                    case 1 -> level == null || stack.is(EnerjoltItemTags.METAL_PRESS_MOLDS);
                    case 2 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0 || slot == 1) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                if(slot == 1)
                    return 1;

                return super.getSlotLimit(slot);
            }
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        if(side == Direction.UP)
            return itemHandlerTopSided;

        return itemHandlerOthersSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<MetalPressRecipe> recipe) {
        if(level == null || !hasRecipe() || !(level instanceof ServerLevel serverLevel))
            return;

         ItemStack pressMold = itemHandler.getStackInSlot(1).copy();
        if(pressMold.isEmpty() && !pressMold.is(EnerjoltItemTags.METAL_PRESS_MOLDS))
            return;

        pressMold.hurtAndBreak(1, serverLevel, null, item -> pressMold.setCount(0));
        itemHandler.setStackInSlot(1, pressMold);
        
        itemHandler.extractItem(0, recipe.value().getInputCount(), false);
        itemHandler.setStackInSlot(2, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(2).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<MetalPressRecipe> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().getResultItem(level.registryAccess()));
    }
}