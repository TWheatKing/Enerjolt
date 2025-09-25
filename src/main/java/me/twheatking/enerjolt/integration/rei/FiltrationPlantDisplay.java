package me.twheatking.enerjolt.integration.rei;

import me.twheatking.enerjolt.block.entity.FiltrationPlantBlockEntity;
import me.twheatking.enerjolt.fluid.EnerjoltFluids;
import me.twheatking.enerjolt.recipe.FiltrationPlantRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;

public record FiltrationPlantDisplay(RecipeHolder<FiltrationPlantRecipe> recipe) implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.of(EnerjoltFluids.DIRTY_WATER.get(), FiltrationPlantBlockEntity.DIRTY_WATER_CONSUMPTION_PER_RECIPE)
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Arrays.stream(recipe.value().getMaxOutputCounts()).filter(itemStack -> !itemStack.isEmpty()).map(EntryIngredients::of).toList();
    }

    @Override
    public CategoryIdentifier<FiltrationPlantDisplay> getCategoryIdentifier() {
        return FiltrationPlantCategory.CATEGORY;
    }
}
