package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.screen.base.ConfigurableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TransformerScreen
        extends ConfigurableEnergyStorageContainerScreen<TransformerMenu> {
    public TransformerScreen(TransformerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        energyMeterX = 80;
    }
}
