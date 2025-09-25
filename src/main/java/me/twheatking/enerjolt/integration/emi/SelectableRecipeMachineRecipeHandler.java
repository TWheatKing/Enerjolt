package me.twheatking.enerjolt.integration.emi;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import me.twheatking.enerjolt.networking.ModMessages;
import me.twheatking.enerjolt.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.twheatking.enerjolt.screen.base.IConfigurableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;

public class SelectableRecipeMachineRecipeHandler<M extends AbstractContainerMenu & IConfigurableMenu>
        implements EmiRecipeHandler<M> {
    private final EmiRecipeCategory recipeCategory;

    public SelectableRecipeMachineRecipeHandler(EmiRecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<M> screen) {
        return new EmiPlayerInventory(new ArrayList<>());
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == recipeCategory;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<M> context) {
        return true;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<M> context) {
        if(!canCraft(recipe, context))
            return false;

        Minecraft.getInstance().setScreen(context.getScreen());

        ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(context.getScreenHandler().getBlockEntity().getBlockPos(), recipe.getId()));

        return true;
    }
}
