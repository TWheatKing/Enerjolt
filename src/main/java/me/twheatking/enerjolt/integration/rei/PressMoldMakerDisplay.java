package me.twheatking.enerjolt.integration.rei;

import me.twheatking.enerjolt.recipe.PressMoldMakerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public record PressMoldMakerDisplay(RecipeHolder<PressMoldMakerRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.of(new ItemStack(Items.CLAY_BALL, recipe.value().getClayCount()))
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutput())
        );
    }

    @Override
    public CategoryIdentifier<PressMoldMakerDisplay> getCategoryIdentifier() {
        return PressMoldMakerCategory.CATEGORY;
    }
}
