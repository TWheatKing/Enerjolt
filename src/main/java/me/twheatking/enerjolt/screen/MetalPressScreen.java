package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetalPressScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<MetalPressMenu> {
    public MetalPressScreen(MetalPressMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.enerjolt.recipe.energy_required_to_finish.txt",
                EJOLTAPI.id("textures/gui/container/metal_press.png"),
                EJOLTAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCraftingActive())
            guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 80, y + 41, 52, 90, menu.getScaledProgressArrowSize(), 10);
    }
}
