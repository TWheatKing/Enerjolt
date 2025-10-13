package me.twheatking.enerjolt.integration.jei;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.recipe.SawmillRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class SawmillCategory implements IRecipeCategory<RecipeHolder<SawmillRecipe>> {
    public static final RecipeType<RecipeHolder<SawmillRecipe>> TYPE = RecipeType.createFromVanilla(SawmillRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;
    private final int width;
    private final int height;

    public SawmillCategory(IGuiHelper helper) {
        ResourceLocation texture = EJOLTAPI.id("textures/gui/container/sawmill.png");
        this.width = 109;
        this.height = 26;
        background = helper.createDrawable(texture, 42, 30, width, height);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EnerjoltBlocks.SAWMILL_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<SawmillRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.enerjolt.sawmill");
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
    public void draw(RecipeHolder<SawmillRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<SawmillRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInput());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 65, 5).addItemStack(recipe.value().getOutput());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 92, 5).
                addItemStacks(recipe.value().getSecondaryOutput().isEmpty()?new ArrayList<>(0):List.of(recipe.value().getSecondaryOutput()));
    }
}