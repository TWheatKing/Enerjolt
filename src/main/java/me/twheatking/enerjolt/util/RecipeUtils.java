package me.twheatking.enerjolt.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public final class RecipeUtils {
    private RecipeUtils() {}

    public static <C extends RecipeInput, T extends Recipe<C>> boolean isIngredientOfAny(Level level, RecipeType<T> recipeType, ItemStack itemStack) {
        List<RecipeHolder<T>> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);

        return recipes.stream().map(RecipeHolder::value).map(Recipe::getIngredients).
                anyMatch(ingredients -> ingredients.stream().anyMatch(ingredient -> ingredient.test(itemStack)));
    }

    public static <C extends RecipeInput, T extends Recipe<C>> boolean isResultOfAny(Level level, RecipeType<T> recipeType, ItemStack itemStack) {
        List<RecipeHolder<T>> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);

        return recipes.stream().map(RecipeHolder::value).map(recipe -> recipe.getResultItem(level.registryAccess())).anyMatch(stack -> ItemStack.isSameItemSameComponents(stack, itemStack));
    }

    public static <C extends RecipeInput, T extends Recipe<C>> boolean isRemainderOfAny(Level level, RecipeType<T> recipeType, C container, ItemStack itemStack) {
        List<RecipeHolder<T>> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);

        return recipes.stream().map(RecipeHolder::value).map(recipe -> recipe.getRemainingItems(container)).
                anyMatch(remainingItems -> remainingItems.stream().anyMatch(item -> ItemStack.isSameItemSameComponents(item, itemStack)));
    }
}
