package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.screen.base.UpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChargingStationScreen
        extends UpgradableEnergyStorageContainerScreen<ChargingStationMenu> {
    public ChargingStationScreen(ChargingStationMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                EJOLTAPI.id("textures/gui/container/upgrade_view/1_energy_capacity_1_range.png"));

        energyMeterX = 80;
    }
}
