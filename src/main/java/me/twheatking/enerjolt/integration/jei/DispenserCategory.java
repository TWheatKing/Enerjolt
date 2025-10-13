package me.twheatking.enerjolt.integration.jei;

import me.twheatking.enerjolt.api.EJOLTAPI;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class DispenserCategory implements IRecipeCategory<DispenserCategory.DispenserRecipe> {
    public static final ResourceLocation UID = EJOLTAPI.id("dispenser");
    public static final RecipeType<DispenserRecipe> TYPE = new RecipeType<>(UID, DispenserRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final int width;
    private final int height;

    public DispenserCategory(IGuiHelper helper) {
        ResourceLocation texture = EJOLTAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 1, 1, 103, 26);
        this.width = 103;
        this.height = 26;

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.DISPENSER));
    }

    @Override
    public RecipeType<DispenserRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.dispenser");
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
    public void draw(DispenserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw the background manually
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, DispenserRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.CATALYST, 1, 5).addIngredients(recipe.tool());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 19, 5).addIngredients(recipe.block());

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 82, 5).addItemStack(recipe.output());
    }

    public record DispenserRecipe(Ingredient tool, Ingredient block, ItemStack output) {}
}