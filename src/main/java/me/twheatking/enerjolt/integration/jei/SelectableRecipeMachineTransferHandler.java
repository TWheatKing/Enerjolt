package me.twheatking.enerjolt.integration.jei;

import me.twheatking.enerjolt.networking.ModMessages;
import me.twheatking.enerjolt.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.twheatking.enerjolt.screen.base.IConfigurableMenu;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SelectableRecipeMachineTransferHandler
        <M extends AbstractContainerMenu & IConfigurableMenu, R extends Recipe<?>>
        implements IRecipeTransferHandler<M, RecipeHolder<R>> {
    private final IRecipeTransferHandlerHelper helper;

    private final Class<? extends M> menuClass;
    private final MenuType<M> menuType;

    public SelectableRecipeMachineTransferHandler(IRecipeTransferHandlerHelper helper, Class<? extends M> menuClass,
                                                  MenuType<M> menuType) {
        this.helper = helper;

        this.menuClass = menuClass;
        this.menuType = menuType;
    }

    @Override
    public Class<? extends M> getContainerClass() {
        return menuClass;
    }

    @Override
    public Optional<MenuType<M>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public RecipeType<RecipeHolder<R>> getRecipeType() {
        return null;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(M container, RecipeHolder<R> recipe, IRecipeSlotsView recipeSlots, Player player,
                                                         boolean maxTransfer, boolean doTransfer) {
        if(!doTransfer)
            return null;

        ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(container.getBlockEntity().getBlockPos(), recipe.id()));

        return null;
    }
}
