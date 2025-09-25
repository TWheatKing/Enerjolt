package me.twheatking.enerjolt.integration.rei;

import me.twheatking.enerjolt.item.EnerjoltItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class DispenserDisplay implements Display {
    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredients.ofItemTag(Tags.Items.TOOLS_SHEAR),
                EntryIngredients.ofItemTag(ItemTags.WOOL)
        );
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(
                EntryIngredients.of(EnerjoltItems.CABLE_INSULATOR.get(), 18)
        );
    }

    @Override
    public CategoryIdentifier<DispenserDisplay> getCategoryIdentifier() {
        return DispenserCategory.CATEGORY;
    }
}
