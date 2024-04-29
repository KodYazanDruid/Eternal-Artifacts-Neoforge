package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.container.AbstractMachineMenu;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AbstractMachineScreen<T extends AbstractMachineMenu> extends AbstractContainerScreen<T> {
    private static final ResourceLocation BARS = new ResourceLocation(MODID, "textures/gui/bars.png");
    @Setter
    protected static ResourceLocation texture = new ResourceLocation(MODID, "textures/gui/anvilinator.png");
    protected int x = (width - imageWidth) / 2;
    protected int y = (height - imageHeight) / 2;

    public AbstractMachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        pGuiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        inventoryLabelX = 25;
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    protected void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(BARS, x, y, 0, 0, 18, 56, 64, 64);
        guiGraphics.blit(BARS, x + 3, y + 53 - menu.getEnergyProgress(), 18, 53 - menu.getEnergyProgress(), 12, menu.getEnergyProgress(), 64, 64);
    }

}
