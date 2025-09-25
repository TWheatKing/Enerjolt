package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class AdvancedChargerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<AdvancedChargerMenu> {
    public AdvancedChargerScreen(AdvancedChargerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_charger.items_energy_left.txt",
                EJOLTAPI.id("textures/gui/container/advanced_charger.png"),
                EJOLTAPI.id("textures/gui/container/upgrade_view/1_energy_capacity.png"));
    }
}
