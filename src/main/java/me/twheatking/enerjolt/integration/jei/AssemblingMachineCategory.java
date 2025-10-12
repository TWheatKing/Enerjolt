package me.twheatking.enerjolt.integration.jei;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.block.EnerjoltBlocks;
import me.twheatking.enerjolt.recipe.AssemblingMachineRecipe;
import me.twheatking.enerjolt.recipe.IngredientWithCount;
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

public class AssemblingMachineCategory implements IRecipeCategory<RecipeHolder<AssemblingMachineRecipe>> {
    public static final RecipeType<RecipeHolder<AssemblingMachineRecipe>> TYPE = RecipeType.createFromVanilla(AssemblingMachineRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;
    private final int width;
    private final int height;

    public AssemblingMachineCategory(IGuiHelper helper) {
        ResourceLocation texture = EJOLTAPI.id("textures/gui/container/assembling_machine.png");
        this.width = 115;
        this.height = 54;
        background = helper.createDrawable(texture, 43, 18, width, height);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EnerjoltBlocks.ASSEMBLING_MACHINE_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<AssemblingMachineRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.enerjolt.assembling_machine");
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
    public void draw(RecipeHolder<AssemblingMachineRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<AssemblingMachineRecipe> recipe, IFocusGroup iFocusGroup) {
        int len = Math.min(recipe.value().getInputs().length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = recipe.value().getInputs()[i];

            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, i == 1?1:(i == 2?37:19), i == 0?1:(i == 3?37:19)).
                    addItemStacks(Arrays.stream(input.input().getItems()).
                            map(itemStack -> itemStack.copyWithCount(input.count())).
                            collect(Collectors.toList()));
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStack(recipe.value().getOutput());
    }
}