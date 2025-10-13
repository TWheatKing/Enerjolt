package me.twheatking.enerjolt.integration.jei;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.recipe.StoneLiquefierRecipe;
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
import net.neoforged.neoforge.fluids.FluidStack;

public class StoneLiquefierCategory implements IRecipeCategory<RecipeHolder<StoneLiquefierRecipe>> {
    public static final RecipeType<RecipeHolder<StoneLiquefierRecipe>> TYPE = RecipeType.createFromVanilla(StoneLiquefierRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;
    private final int width;
    private final int height;

    public StoneLiquefierCategory(IGuiHelper helper) {
        ResourceLocation texture = EJOLTAPI.id("textures/gui/recipe/misc_gui.png");
        this.width = 85;
        this.height = 26;
        background = helper.createDrawable(texture, 1, 77, width, height);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EnerjoltBlocks.STONE_LIQUEFIER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<StoneLiquefierRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.enerjolt.stone_liquefier");
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
    public void draw(RecipeHolder<StoneLiquefierRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<StoneLiquefierRecipe> recipe, IFocusGroup iFocusGroup) {
        FluidStack output = recipe.value().getOutput();

        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 5).addIngredients(recipe.value().getInput());

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 64, 5).addFluidStack(output.getFluid(),
                output.getAmount(), output.getComponentsPatch());
    }
}