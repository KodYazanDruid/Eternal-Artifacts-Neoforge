package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import javax.annotation.Nonnull;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractModContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    //Margin: 5px
    //Corner: 5px * 5px
    //Sides: 5px * -px
    //Inside of Template: 166px * 156px
    //Total Size: 176px * 166px
    @Nonnull
    @Setter
    private static ResourceLocation texture = new ResourceLocation(MODID, "textures/gui/template.png");
    protected int x;
    protected int y;
    @Setter
    private boolean isModular = false;

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
        renderBackground(gui, x, y);
        renderSlots(gui, x, y);
    }

    private void renderBackground(GuiGraphics gui, int x, int y) {
        if(!isModular) gui.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
        else {
            blitCorners(gui, x, y);
            blitSides(gui, x, y);
            blitInside(gui, x, y);
        }
    }

    private void blitCorners(GuiGraphics gui, int x, int y) {
        gui.blit(texture, x, y, 0, 0, 5, 5);
        gui.blit(texture, x + imageWidth - 5, y, 171, 0, 5, 5);
        gui.blit(texture, x, y + imageHeight - 5, 0, 161, 5, 5);
        gui.blit(texture, x + imageWidth - 5, y + imageHeight - 5, 171, 161, 5, 5);
    }

    private void blitSides(GuiGraphics gui, int x, int y) {
        iterateXSide(gui, x, y);
        iterateYSide(gui, x, y);
    }

    private void iterateXSide(GuiGraphics gui, int x, int y) {
        int totalWidth = imageWidth - 10;
        int iteration = totalWidth / 166;
        int remaining = totalWidth % 166;
        if(iteration > 0) {
            for(int i = 0; i < iteration; i++) {
                gui.blit(texture, x + 5 + (166 * i), y, 5, 0, 166, 5);
                gui.blit(texture, x + 5 + (166 * i), y + imageHeight - 5, 5, 161, 166, 5);
            }
            if(remaining > 0) {
                gui.blit(texture, x + 5 + (166 * iteration), y, 5, 0, remaining, 5);
                gui.blit(texture, x + 5 + (166 * iteration), y + imageHeight - 5, 5, 161, remaining, 5);
            }
        } else {
            gui.blit(texture, x + 5, y, 5, 0, remaining, 5);
            gui.blit(texture, x + 5, y + imageHeight - 5, 5, 161, remaining, 5);
        }
    }

    private void iterateYSide(GuiGraphics gui, int x, int y) {
        int totalHeight = imageHeight - 10;
        int iteration = totalHeight / 156;
        int remaining = totalHeight % 156;
        if(iteration > 0) {
            for(int i = 0; i < iteration; i++) {
                gui.blit(texture, x, y + 5 + (156 * i), 0, 5, 5, 156);
                gui.blit(texture, x + imageWidth - 5, y + 5 + (156 * i), 171, 5, 5, 156);
            }
            if (remaining > 0) {
                gui.blit(texture, x, y + 5 + (156 * iteration), 0, 5, 5, remaining);
                gui.blit(texture, x + imageWidth - 5, y + 5 + (156 * iteration), 171, 5, 5, remaining);
            }
        }else {
            gui.blit(texture, x, y + 5, 0, 5, 5, remaining);
            gui.blit(texture, x + imageWidth - 5, y + 5, 171, 5, 5, remaining);
        }
    }

    private void blitInside(GuiGraphics gui, int x, int y) {
        int totalWidth = imageWidth - 10;
        int iterationX = totalWidth / 166;
        int remainingX = totalWidth % 166;
        int totalHeight = imageHeight - 10;
        int iterationY = totalHeight / 156;
        int remainingY = totalHeight % 156;

        if(iterationX <= 0 && iterationY <= 0) gui.blit(texture, x + 5, y + 5, 5, 5, remainingX, remainingY);

        if (iterationX > 0 && iterationY <= 0) {
            for (int i = 0; i < iterationX; i++) gui.blit(texture, x + 5 + (iterationX * i), y + 5, 5, 5, 166, remainingY);
            if (remainingX > 0) gui.blit(texture, x + 5 + (166 * iterationX), y + 5, 5, 5, remainingX, remainingY);
        }

        if (iterationX <= 0 && iterationY > 0) {
            for (int i = 0; i < iterationY; i++) gui.blit(texture, x + 5, y + 5 + (156 * i), 5, 5, remainingX, 156);
            if (remainingY > 0) gui.blit(texture, x + 5, y + 5 + (156 * iterationY), 5, 5, remainingX, remainingY);
        }

        if (iterationX > 0 && iterationY > 0) {
            for (int i = 0; i < iterationX; i++) {
                for (int j = 0; j < iterationY; j++) {
                    gui.blit(texture, x + 5 + (166 * i), y + 5 + (156 * j), 5, 5, 166, 156);
                }
            }

            if (remainingX > 0) {
                for (int i = 0; i < iterationY; i++)
                    gui.blit(texture, x + 5 + (166 * iterationX), y + 5 + (156 * i), 5, 5, remainingX, 156);
            }

            if (remainingY > 0) {
                for (int i = 0; i < iterationX; i++)
                    gui.blit(texture, x + 5 + (166 * i), y + 5 + (156 * iterationY), 5, 5, 166, remainingY);
            }

            if (remainingX > 0 && remainingY > 0) {
                gui.blit(texture, x + 5 + (166 * iterationX), y + 5 + (156 * iterationY), 5, 5, remainingX, remainingY);
            }

        }
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
