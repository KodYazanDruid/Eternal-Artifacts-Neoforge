package com.sonamorningstar.eternalartifacts.client.gui.screen.util;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class GuiDrawer {
    private static final ResourceLocation texture = new ResourceLocation(MODID, "textures/gui/template.png");
    protected static final ResourceLocation bars = new ResourceLocation(MODID, "textures/gui/bars.png");

    public static void drawBackground(GuiGraphics gui, int x, int y, int width, int height) {
        blitCorners(gui, x, y, width, height);
        blitSides(gui, x, y, width, height);
        blitInside(gui, x, y, width, height);
    }
    public static void drawItemSlot(GuiGraphics gui, int x, int y) {
        gui.blitSprite(new ResourceLocation("container/slot"), x, y, 0, 18, 18);
    }
    public static void drawEmptyArrow(GuiGraphics gui, int x, int y) {
        gui.blit(bars, x, y, 0, 56, 22, 15);
    }
    public static void drawFluidWithTank(GuiGraphics gui, int x, int y, FluidStack stack, int percentage) {
        drawTiledFluid(gui, x, y, 12, 50, percentage, stack);
        drawEmptyTank(gui, x, y);
    }
    public static void drawEmptyTank(GuiGraphics gui, int x, int y) {
        gui.blit(bars, x, y, 30, 0, 18, 56);
    }
    public static void drawFluidWithSmallTank(GuiGraphics gui, int x, int y, FluidStack stack, int percentage) {
        drawTiledFluid(gui, x, y, 12, 12, percentage, stack);
        drawEmptySmallTank(gui, x, y);
    }
    public static void drawEmptySmallTank(GuiGraphics gui, int x, int y) {
        gui.blit(bars, x, y, 66, 37, 18, 18);
    }
    public static void drawTiledFluid(GuiGraphics gui, int x, int y, int width, int height, int percentage, FluidStack stack) {
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(stack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(stack);
        if(stillTexture != null) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
            int tintColor = fluidTypeExtensions.getTintColor(stack);
            float alpha = ((tintColor >> 24) & 0xFF) / 255f;
            float red = ((tintColor >> 16) & 0xFF) / 255f;
            float green = ((tintColor >> 8) & 0xFF) / 255f;
            float blue = ((tintColor) & 0xFF) / 255f;
            gui.setColor(red, green, blue, alpha);
            gui.blitTiledSprite(
                    sprite,
                    x + 3,
                    y + 3 + height - percentage,
                    0, //z - layer
                    width,
                    percentage,
                    0, // these are offsets for atlas x
                    0, //  y
                    16, // Sprite dimensions to cut.
                    16, //
                    16, // Resolutions. 16x16 works fine.
                    16);
            gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
    public static void draw(GuiGraphics gui, ResourceLocation location, int x, int y, int width, int height) {
        draw(gui, location, x, y, 0, 0, width, height);
    }
    public static void draw(GuiGraphics gui, ResourceLocation location, int x, int y, int u, int v, int width, int height) {
        gui.blit(location, x, y, u, v, width, height);
    }

    //region Background drawing.
    private static void blitCorners(GuiGraphics gui, int x, int y, int width, int height) {
        gui.blit(texture, x, y, 0, 0, 5, 5);
        gui.blit(texture, x + width - 5, y, 171, 0, 5, 5);
        gui.blit(texture, x, y + height - 5, 0, 161, 5, 5);
        gui.blit(texture, x + width - 5, y + height - 5, 171, 161, 5, 5);
    }
    private static void blitSides(GuiGraphics gui, int x, int y, int width, int height) {
        iterateXSide(gui, x, y, width, height);
        iterateYSide(gui, x, y, width, height);
    }
    private static void iterateXSide(GuiGraphics gui, int x, int y, int width, int height) {
        int totalWidth = width - 10;
        int iteration = totalWidth / 166;
        int remaining = totalWidth % 166;
        if(iteration > 0) {
            for(int i = 0; i < iteration; i++) {
                gui.blit(texture, x + 5 + (166 * i), y, 5, 0, 166, 5);
                gui.blit(texture, x + 5 + (166 * i), y + height - 5, 5, 161, 166, 5);
            }
            if(remaining > 0) {
                gui.blit(texture, x + 5 + (166 * iteration), y, 5, 0, remaining, 5);
                gui.blit(texture, x + 5 + (166 * iteration), y + height - 5, 5, 161, remaining, 5);
            }
        } else {
            gui.blit(texture, x + 5, y, 5, 0, remaining, 5);
            gui.blit(texture, x + 5, y + height - 5, 5, 161, remaining, 5);
        }
    }
    private static void iterateYSide(GuiGraphics gui, int x, int y, int width, int height) {
        int totalHeight = height - 10;
        int iteration = totalHeight / 156;
        int remaining = totalHeight % 156;
        if(iteration > 0) {
            for(int i = 0; i < iteration; i++) {
                gui.blit(texture, x, y + 5 + (156 * i), 0, 5, 5, 156);
                gui.blit(texture, x + width - 5, y + 5 + (156 * i), 171, 5, 5, 156);
            }
            if (remaining > 0) {
                gui.blit(texture, x, y + 5 + (156 * iteration), 0, 5, 5, remaining);
                gui.blit(texture, x + width - 5, y + 5 + (156 * iteration), 171, 5, 5, remaining);
            }
        }else {
            gui.blit(texture, x, y + 5, 0, 5, 5, remaining);
            gui.blit(texture, x + width - 5, y + 5, 171, 5, 5, remaining);
        }
    }
    private static void blitInside(GuiGraphics gui, int x, int y, int width, int height) {
        int totalWidth = width - 10;
        int iterationX = totalWidth / 166;
        int remainingX = totalWidth % 166;
        int totalHeight = height - 10;
        int iterationY = totalHeight / 156;
        int remainingY = totalHeight % 156;

        if(iterationX <= 0 && iterationY <= 0) gui.blit(texture, x + 5, y + 5, 5, 5, remainingX, remainingY);

        if (iterationX > 0 && iterationY <= 0) {
            for (int i = 0; i < iterationX; i++) gui.blit(texture, x + 5 + (166 * i), y + 5, 5, 5, 166, remainingY);
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

    public static void drawEnergyBar(GuiGraphics gui, ItemStack stack, int x, int y) {
        if(stack.isEmpty()) return;
        IEnergyStorage energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy == null) return;
        draw(gui, bars, x, y, 0, 0, 18, 56);
        int energyHeight = energy.getEnergyStored() * 53 / energy.getMaxEnergyStored();
        draw(gui, bars, x + 3, y + 53 - energyHeight, 18, 53 - energyHeight, 12, energyHeight);
    }
    //endregion
    
    //region Text drawing.
    public static void renderScrollingString(
        GuiGraphics gui, Font font, Component text,
        int minX, int minY, int maxX, int maxY, int color
    ) {
        renderScrollingString(gui, font, text, minX, minY, maxX, maxY, color, false);
    }
    public static void renderScrollingString(
        GuiGraphics gui, Font font, Component text,
        int minX, int minY, int maxX, int maxY, int color, boolean dropShadow) {
        renderScrollingStringForPanel(gui, font, text, minX, minY, maxX, maxY, 0, color, dropShadow);
    }
    public static void renderScrollingStringForPanel(
        GuiGraphics gui, Font font, Component text,
        int minX, int minY, int maxX, int maxY, double scroll, int color
    ) {
        renderScrollingStringForPanel(gui, font, text, minX, minY, maxX, maxY, scroll, color, false);
    }
    public static void renderScrollingStringForPanel(
        GuiGraphics gui, Font font, Component text,
        int minX, int minY, int maxX, int maxY, double scroll, int color, boolean dropShadow
    ) {
        int textWidth = font.width(text);
        int deltaX = maxX - minX;
        int middleX = deltaX / 2;
        int scrolledMinY = minY - (int)scroll;
        int scrolledMaxY = maxY - (int)scroll;
        int wHeight = maxY - minY;
        int j = minY + (wHeight - 8) / 2;
        if (textWidth > deltaX) {
            int overflow = textWidth - deltaX;
            double d0 = (double) Util.getMillis() / 1000.0;
            double d1 = Math.max((double)overflow * 0.5, 3.0);
            double d2 = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d0 / d1)) / 2.0 + 0.5;
            double d3 = Mth.lerp(d2, 0.0, overflow);
            gui.enableScissor(minX, scrolledMinY, maxX, scrolledMaxY);
            gui.drawString(font, text, minX - (int)d3, j, color, dropShadow);
            gui.disableScissor();
        } else {
            int i1 = Mth.clamp(middleX, minX + textWidth / 2, maxX - textWidth / 2);
            drawCenteredString(gui, font, text, i1, j, color, dropShadow);
        }
    }
    
    public static void drawCenteredString(GuiGraphics gui, Font pFont, Component pText, int pX, int pY, int pColor, boolean dropShadow) {
        gui.drawString(pFont, pText, pX - pFont.width(pText) / 2, pY, pColor, dropShadow);
    }
    //endregion
}
