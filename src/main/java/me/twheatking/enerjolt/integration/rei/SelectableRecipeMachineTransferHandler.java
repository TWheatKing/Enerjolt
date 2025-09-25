package me.twheatking.enerjolt.integration.rei;

import me.twheatking.enerjolt.networking.ModMessages;
import me.twheatking.enerjolt.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.twheatking.enerjolt.screen.base.IConfigurableMenu;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class SelectableRecipeMachineTransferHandler
        <M extends AbstractContainerMenu & IConfigurableMenu, R extends Recipe<?>>
        implements TransferHandler {
    private final Class<? extends M> menuClass;
    private final Class<? extends R> recipeClass;

    public SelectableRecipeMachineTransferHandler(Class<? extends M> menuClass, Class<? extends R> recipeClass) {
        this.menuClass = menuClass;
        this.recipeClass = recipeClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result handle(Context context) {
        if(context.getMenu() == null || !menuClass.isAssignableFrom(context.getMenu().getClass()))
            return Result.createNotApplicable();

        M container = (M)context.getMenu();

        Display display = context.getDisplay();
        Object origin = DisplayRegistry.getInstance().getDisplayOrigin(display);
        if(!(origin instanceof RecipeHolder<?> recipeEntry) || !recipeClass.isAssignableFrom(recipeEntry.value().getClass()))
            return Result.createNotApplicable();

        if(!context.isActuallyCrafting())
            return Result.createSuccessful().blocksFurtherHandling();

        ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(container.getBlockEntity().getBlockPos(), recipeEntry.id()));

        return Result.createSuccessful().blocksFurtherHandling();
    }
}
