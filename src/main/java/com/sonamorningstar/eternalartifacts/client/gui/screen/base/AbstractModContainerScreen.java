package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nonnull;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractModContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    @Nonnull
    @Setter
    protected static ResourceLocation texture = new ResourceLocation(MODID, "textures/gui/template.png");
    protected int x;
    protected int y;
    public AbstractModContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mX, int mY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        this.x = (width - imageWidth) / 2;
        this.y = (height - imageHeight) / 2;
        gui.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
        renderSlots(gui, x, y);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        renderBackground(gui, mx, my, partialTick);
        super.render(gui, mx, my, partialTick);
        renderTooltip(gui, mx, my);
    }

    protected void renderSlots(GuiGraphics gui, int x, int y) {
        for(Slot slot : menu.slots) {
            gui.blitSprite(new ResourceLocation("container/slot"), x + slot.x-1, y + slot.y-1, 0, 18, 18);
        }
    }
}
