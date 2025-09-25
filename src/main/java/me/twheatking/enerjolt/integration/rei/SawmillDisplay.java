package me.twheatking.enerjolt.integration.rei;

import me.twheatking.enerjolt.recipe.SawmillRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public record SawmillDisplay(RecipeHolder<SawmillRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofIngredient(recipe.value().getInput())
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(recipe.value().getOutput()),
                EntryIngredients.of(recipe.value().getSecondaryOutput())
        );
    }

    @Override
    public CategoryIdentifier<SawmillDisplay> getCategoryIdentifier() {
        return SawmillCategory.CATEGORY;
    }
}
