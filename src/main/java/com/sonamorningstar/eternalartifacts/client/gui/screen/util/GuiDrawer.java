package com.sonamorningstar.eternalartifacts.client.gui.screen.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
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
    public static void drawFluidWithTank(GuiGraphics gui, int x, int y, FluidStack stack, int percentage) {
        drawTiledFluid(gui, x, y, 12, 50, percentage, stack);
        gui.blit(bars, x, y, 30, 0, 18, 56);
    }
    public static void drawFluidWithSmallTank(GuiGraphics gui, int x, int y, FluidStack stack, int percentage) {
        drawTiledFluid(gui, x, y, 12, 12, percentage, stack);
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
    //endregion
}
