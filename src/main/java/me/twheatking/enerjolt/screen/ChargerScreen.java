package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ChargerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<ChargerMenu> {
    public ChargerScreen(ChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.charger.item_energy_left.txt",
                EJOLTAPI.id("textures/gui/container/charger.png"),
                EJOLTAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }
}
