package me.twheatking.enerjolt.integration.jei;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.recipe.CompressorRecipe;
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

import java.util.Arrays;
import java.util.stream.Collectors;

public class CompressorCategory implements IRecipeCategory<RecipeHolder<CompressorRecipe>> {
    public static final RecipeType<RecipeHolder<CompressorRecipe>> TYPE = RecipeType.createFromVanilla(CompressorRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;
    private final int width;
    private final int height;

    public CompressorCategory(IGuiHelper helper) {
        ResourceLocation texture = EJOLTAPI.id("textures/gui/container/compressor.png");
        this.width = 98;
        this.height = 26;
        background = helper.createDrawable(texture, 47, 30, width, height);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EnerjoltBlocks.COMPRESSOR_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<CompressorRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.enerjolt.compressor");
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
    public void draw(RecipeHolder<CompressorRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<CompressorRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStacks(
                Arrays.stream(recipe.value().getInput().getItems()).
                        map(itemStack -> itemStack.copyWithCount(recipe.value().getInputCount())).
                        collect(Collectors.toList()));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 77, 5).addItemStack(recipe.value().getOutput());
    }
}