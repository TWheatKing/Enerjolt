package me.twheatking.enerjolt.screen.base;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface ISelectableRecipeMachineMenu<R extends Recipe<?>> {
    RecipeHolder<R> getCurrentRecipe();
}
