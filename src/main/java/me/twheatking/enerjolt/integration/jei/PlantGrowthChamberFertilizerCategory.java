package me.twheatking.enerjolt.integration.jei;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.recipe.PlantGrowthChamberFertilizerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class PlantGrowthChamberFertilizerCategory implements IRecipeCategory<RecipeHolder<PlantGrowthChamberFertilizerRecipe>> {
    public static final RecipeType<RecipeHolder<PlantGrowthChamberFertilizerRecipe>> TYPE = RecipeType.createFromVanilla(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable fertlizerSlot;
    private final IDrawable icon;
    private final int width;
    private final int height;

    public PlantGrowthChamberFertilizerCategory(IGuiHelper helper) {
        ResourceLocation texture = EJOLTAPI.id("textures/gui/container/plant_growth_chamber.png");
        fertlizerSlot = helper.createDrawable(texture, 34, 34, 18, 18);
        this.width = 144;
        this.height = 30;
        background = helper.createBlankDrawable(width, height);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EnerjoltBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<PlantGrowthChamberFertilizerRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipes.enerjolt.plant_growth_chamber_fertilizer");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<PlantGrowthChamberFertilizerRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.value().getInput());
    }

    @Override
    public void draw(RecipeHolder<PlantGrowthChamberFertilizerRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw the background (blank) first
        background.draw(guiGraphics, 0, 0);
        
        // Draw the fertilizer slot texture
        fertlizerSlot.draw(guiGraphics, 0, 0);

        // Draw speed multiplier text
        Font font = Minecraft.getInstance().font;
        Component component = Component.translatable("recipes.enerjolt.plant_growth_chamber_fertilizer.speed_multiplier", recipe.value().getSpeedMultiplier());
        int textWidth = font.width(component);

        guiGraphics.drawString(Minecraft.getInstance().font, component, 144 - textWidth, 5, 0xFFFFFFFF, false);

        // Draw energy consumption multiplier text
        component = Component.translatable("recipes.enerjolt.plant_growth_chamber_fertilizer.energy_consumption_multiplier", recipe.value().getEnergyConsumptionMultiplier());
        textWidth = font.width(component);

        guiGraphics.drawString(Minecraft.getInstance().font, component, 144 - textWidth, 22, 0xFFFFFFFF, false);
    }
}