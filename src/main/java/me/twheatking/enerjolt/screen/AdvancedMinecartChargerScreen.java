package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.screen.base.EnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedMinecartChargerScreen extends EnergyStorageContainerScreen<AdvancedMinecartChargerMenu> {
    public AdvancedMinecartChargerScreen(AdvancedMinecartChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        energyMeterX = 80;
    }
}
