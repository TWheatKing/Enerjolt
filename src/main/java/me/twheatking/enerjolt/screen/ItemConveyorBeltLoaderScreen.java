package me.twheatking.enerjolt.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.twheatking.enerjolt.api.EJOLTAPI;
import me.twheatking.enerjolt.screen.base.EnerjoltBaseContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemConveyorBeltLoaderScreen extends EnerjoltBaseContainerScreen<ItemConveyorBeltLoaderMenu> {
    private final ResourceLocation TEXTURE;

    public ItemConveyorBeltLoaderScreen(ItemConveyorBeltLoaderMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EJOLTAPI.id("textures/gui/container/generic_1x1.png");
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
