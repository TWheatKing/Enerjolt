package me.twheatking.enerjolt.screen;

import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IndustrialGreenhouseScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<IndustrialGreenhouseMenu> {

    public IndustrialGreenhouseScreen(IndustrialGreenhouseMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.enerjolt.industrial_greenhouse.energy_required_to_finish.txt",
                EJOLTAPI.id("textures/gui/container/industrial_greenhouse.png"),
                EJOLTAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected void renderBgNormalView(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(guiGraphics, x, y);
        renderStatusIndicators(guiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isProcessing()) {
            guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 70, y + 35, 0, 79, menu.getScaledProgressArrowSize(), 16);
        }
    }

    private void renderStatusIndicators(GuiGraphics guiGraphics, int x, int y) {
        // Draw a green indicator if multiblock is formed
        if(menu.isFormed()) {
            guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 8, y + 8, 176, 0, 8, 8);
        } else {
            // Draw red indicator if not formed
            guiGraphics.blit(MACHINE_SPRITES_TEXTURE, x + 8, y + 8, 184, 0, 8, 8);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Add multiblock status tooltip
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Status indicator tooltip
        if(mouseX >= x + 8 && mouseX <= x + 16 && mouseY >= y + 8 && mouseY <= y + 16) {
            if(menu.isFormed()) {
                guiGraphics.renderTooltip(font, Component.translatable("tooltip.enerjolt.multiblock.formed"), mouseX, mouseY);
            } else {
                guiGraphics.renderTooltip(font, Component.translatable("tooltip.enerjolt.multiblock.not_formed"), mouseX, mouseY);
            }
        }
    }
}