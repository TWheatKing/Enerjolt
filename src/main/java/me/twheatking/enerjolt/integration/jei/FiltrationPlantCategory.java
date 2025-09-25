package me.twheatking.enerjolt.integration.jei;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.block.entity.FiltrationPlantBlockEntity;
import me.twheatking.enerjolt.fluid.EnerjoltFluids;
import me.twheatking.enerjolt.recipe.FiltrationPlantRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Locale;

public class FiltrationPlantCategory implements IRecipeCategory<RecipeHolder<FiltrationPlantRecipe>> {
    public static final RecipeType<RecipeHolder<FiltrationPlantRecipe>> TYPE = RecipeType.createFromVanilla(FiltrationPlantRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public FiltrationPlantCategory(IGuiHelper helper) {
        ResourceLocation texture = EJOLTAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 105, 112, 26);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EnerjoltBlocks.FILTRATION_PLANT_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<FiltrationPlantRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.filtration_plant");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<FiltrationPlantRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addFluidStack(EnerjoltFluids.DIRTY_WATER.get(),
                FiltrationPlantBlockEntity.DIRTY_WATER_CONSUMPTION_PER_RECIPE);

        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addItemStack(outputEntries[0]).
                addRichTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 92, 5).
                addItemStacks(outputEntries[1].isEmpty()? List.of():List.of(outputEntries[1])).
                addRichTooltipCallback((view, tooltip) -> {
                    if(view.isEmpty())
                        return;

                    tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                    double[] percentages = recipe.value().getSecondaryOutput().percentages();
                    for(int i = 0;i < percentages.length;i++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
                });
    }
}
