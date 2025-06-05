package com.sonamorningstar.eternalartifacts.client.gui.screen.util;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public final class GuiDrawer {
    private static final ResourceLocation texture = new ResourceLocation(MODID, "textures/gui/template.png");
    public static final ResourceLocation default_edge = new ResourceLocation(MODID, "textures/gui/default_edge.png");
    public static final ResourceLocation dark_edge = new ResourceLocation(MODID, "textures/gui/dark_edge.png");
    private static final ResourceLocation bars = new ResourceLocation(MODID, "textures/gui/bars.png");

    public static void drawDefaultBackground(GuiGraphics gui, int x, int y, int width, int height) {
        drawBackground(gui, texture, x, y, width, height, 176, 166, 5);
    }
    
    public static void drawBackground(
            GuiGraphics gui, ResourceLocation texture, int x, int y,
            int width, int height, int textureWidth, int textureHeight, int edgeWidth
    ) {
        blitCorners(gui, texture, x, y, width, height, textureWidth, textureHeight, 256, 256, edgeWidth);
        blitSides(gui, texture, x, y, width, height, textureWidth, textureHeight, 256, 256, edgeWidth);
        blitInside(gui, texture, x + edgeWidth, y + edgeWidth,
            width - 2 * edgeWidth, height - 2 * edgeWidth,
            textureWidth - 2 * edgeWidth, textureHeight - 2 * edgeWidth,
            256, 256, edgeWidth);
    }
    
    public static void drawTiledBackground(GuiGraphics gui, Block texture, int x, int y, int width, int height) {
        //TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(texture.defaultBlockState()).getParticleIcon(ModelData.EMPTY);
        ResourceLocation blockRL = BuiltInRegistries.BLOCK.getKey(texture);
        ResourceLocation textureRL = new ResourceLocation(blockRL.getNamespace(), "textures/block/" + blockRL.getPath() + ".png");
        drawTiledBackground(gui, dark_edge, textureRL, x, y, width, height, 16, 16, 16, 16, 3);
    }
    
    public static void drawTiledBackground(GuiGraphics gui, String textureName, int x, int y, int width, int height) {
        String[] split = textureName.split(":");
        ResourceLocation textureRL = new ResourceLocation(split[0], "textures/" + split[1] + ".png");
        drawTiledBackground(gui, dark_edge, textureRL, x, y, width, height, 16, 16, 16, 16, 3);
    }
    
    public static void drawTiledBackground(
        GuiGraphics gui, ResourceLocation edgeTexture, ResourceLocation insideTexture,
        int x, int y, int width, int height, int textureWidth, int textureHeight,
        int totalSpriteWidth, int totalSpriteHeight, int edgeWidth
    ) {
        blitCorners(gui, edgeTexture, x, y, width, height, 3 * edgeWidth, 3 * edgeWidth, 3 * edgeWidth, 3 * edgeWidth, edgeWidth);
        blitSides(gui, edgeTexture, x, y, width, height, 3 * edgeWidth, 3 * edgeWidth, 3 * edgeWidth, 3 * edgeWidth, edgeWidth);
        blitInside(gui, insideTexture, x + edgeWidth, y + edgeWidth,
            width - 2 * edgeWidth, height - 2 * edgeWidth, textureWidth, textureHeight,
            totalSpriteWidth, totalSpriteHeight, 0);
    }
    
    public static void drawFramedBackground(GuiGraphics gui, int x, int y, int width, int height, int edgeThickness,
                                            int insideColor, int edgeColor) {
        drawFramedBackground(gui, x, y, width, height, edgeThickness, insideColor, edgeColor, edgeColor);
    }
    public static void drawFramedBackground(GuiGraphics gui, int x, int y, int width, int height, int edgeThickness,
                                    int insideColor, int edgeColor1, int edgeColor2) {
        gui.fillGradient(x, y, x + width - edgeThickness, y + edgeThickness, edgeColor1, edgeColor1);
        gui.fillGradient(x, y, x + edgeThickness, y + height, edgeColor1, edgeColor1);
        gui.fillGradient(x + width - edgeThickness, y, x + width, y + height, edgeColor2, edgeColor2);
        gui.fillGradient(x + edgeThickness, y + height - edgeThickness, x + width, y + height, edgeColor2, edgeColor2);
        gui.fillGradient(x + edgeThickness, y + edgeThickness, x + width - edgeThickness, y + height - edgeThickness, insideColor, insideColor);
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
    private static void blitCorners(
        GuiGraphics gui, ResourceLocation texture, int x, int y,
        int width, int height, int textureWidth, int textureHeight,
        int totalSpriteWidth, int totalSpriteHeight, int edgeWidth
    ) {
        gui.blit(texture, x, y, 0, 0, edgeWidth, edgeWidth, totalSpriteWidth, totalSpriteHeight);
        gui.blit(texture, x + width - edgeWidth, y, textureWidth - edgeWidth, 0, edgeWidth, edgeWidth, totalSpriteWidth, totalSpriteHeight);
        gui.blit(texture, x, y + height - edgeWidth, 0, textureHeight - edgeWidth, edgeWidth, edgeWidth, totalSpriteWidth, totalSpriteHeight);
        gui.blit(texture, x + width - edgeWidth, y + height - edgeWidth, textureWidth - edgeWidth, textureHeight - edgeWidth, edgeWidth, edgeWidth, totalSpriteWidth, totalSpriteHeight);
    }
    private static void blitSides(
        GuiGraphics gui, ResourceLocation texture, int x, int y,
        int width, int height, int textureWidth, int textureHeight,
        int totalSpriteWidth, int totalSpriteHeight, int edgeWidth
    ) {
        iterateXSide(gui, texture, x, y, width, height, textureWidth, textureHeight, totalSpriteWidth, totalSpriteHeight, edgeWidth);
        iterateYSide(gui, texture, x, y, width, height, textureWidth, textureHeight, totalSpriteWidth, totalSpriteHeight, edgeWidth);
    }
    
    private static void iterateXSide(
        GuiGraphics gui, ResourceLocation texture, int x, int y,
        int width, int height, int textureWidth, int textureHeight,
        int totalSpriteWidth, int totalSpriteHeight, int edgeHeight
    ) {
        int padding = 2 * edgeHeight;
        int totalWidth = width - padding;
        int reelWidth = textureWidth - padding;
        int iteration = totalWidth / reelWidth;
        int remaining = totalWidth % reelWidth;
        if(iteration > 0) {
            for(int i = 0; i < iteration; i++) {
                gui.blit(texture, x + edgeHeight + (reelWidth * i), y, edgeHeight, 0, reelWidth, edgeHeight, totalSpriteWidth, totalSpriteHeight);
                gui.blit(texture, x + edgeHeight + (reelWidth * i), y + height - edgeHeight, edgeHeight, textureHeight - edgeHeight, reelWidth, edgeHeight, totalSpriteWidth, totalSpriteHeight);
            }
            if(remaining > 0) {
                gui.blit(texture, x + edgeHeight + (reelWidth * iteration), y, edgeHeight, 0, remaining, edgeHeight, totalSpriteWidth, totalSpriteHeight);
                gui.blit(texture, x + edgeHeight + (reelWidth * iteration), y + height - edgeHeight, edgeHeight, textureHeight - edgeHeight, remaining, edgeHeight, totalSpriteWidth, totalSpriteHeight);
            }
        } else {
            gui.blit(texture, x + edgeHeight, y, edgeHeight, 0, remaining, edgeHeight, totalSpriteWidth, totalSpriteHeight);
            gui.blit(texture, x + edgeHeight, y + height - edgeHeight, edgeHeight, textureHeight - edgeHeight, remaining, edgeHeight, totalSpriteWidth, totalSpriteHeight);
        }
    }
    private static void iterateYSide(
        GuiGraphics gui, ResourceLocation texture, int x, int y,
        int width, int height, int textureWidth, int textureHeight,
        int totalSpriteWidth, int totalSpriteHeight, int edgeWidth
    ) {
        int padding = 2 * edgeWidth;
        int reelHeight = textureHeight - padding;
        int totalHeight = height - padding;
        int iteration = totalHeight / reelHeight;
        int remaining = totalHeight % reelHeight;
        if(iteration > 0) {
            for(int i = 0; i < iteration; i++) {
                gui.blit(texture, x, y + edgeWidth + (reelHeight * i), 0, edgeWidth, edgeWidth, reelHeight, totalSpriteWidth, totalSpriteHeight);
                gui.blit(texture, x + width - edgeWidth, y + edgeWidth + (reelHeight * i), textureWidth - edgeWidth, edgeWidth, edgeWidth, reelHeight, totalSpriteWidth, totalSpriteHeight);
            }
            if (remaining > 0) {
                gui.blit(texture, x, y + edgeWidth + (reelHeight * iteration), 0, edgeWidth, edgeWidth, remaining, totalSpriteWidth, totalSpriteHeight);
                gui.blit(texture, x + width - edgeWidth, y + edgeWidth + (reelHeight * iteration), textureWidth - edgeWidth, edgeWidth, edgeWidth, remaining, totalSpriteWidth, totalSpriteHeight);
            }
        }else {
            gui.blit(texture, x, y + edgeWidth, 0, edgeWidth, edgeWidth, remaining, totalSpriteWidth, totalSpriteHeight);
            gui.blit(texture, x + width - edgeWidth, y + edgeWidth, textureWidth - edgeWidth, edgeWidth, edgeWidth, remaining, totalSpriteWidth, totalSpriteHeight);
        }
    }
    private static void blitInside(
            GuiGraphics gui, ResourceLocation texture,
            int x, int y, int width, int height,
            int textureWidth, int textureHeight,
            int totalSpriteWidth, int totalSpriteHeight,
            int edgeWidth
    ) {
		int iterationX = width / textureWidth;
        int remainingX = width % textureWidth;
		int iterationY = height / textureHeight;
        int remainingY = height % textureHeight;

        if(iterationX <= 0 && iterationY <= 0) gui.blit(texture, x, y, edgeWidth, edgeWidth, remainingX, remainingY);

        if (iterationX > 0 && iterationY <= 0) {
            for (int i = 0; i < iterationX; i++) gui.blit(texture, x + (textureWidth * i), y, edgeWidth, edgeWidth, textureWidth, remainingY, totalSpriteWidth, totalSpriteHeight);
            if (remainingX > 0) gui.blit(texture, x + (textureWidth * iterationX), y, edgeWidth, edgeWidth, remainingX, remainingY, totalSpriteWidth, totalSpriteHeight);
        }

        if (iterationX <= 0 && iterationY > 0) {
            for (int i = 0; i < iterationY; i++) gui.blit(texture, x, y + (textureHeight * i), edgeWidth, edgeWidth, remainingX, textureHeight, totalSpriteWidth, totalSpriteHeight);
            if (remainingY > 0) gui.blit(texture, x, y + (textureHeight * iterationY), edgeWidth, edgeWidth, remainingX, remainingY, totalSpriteWidth, totalSpriteHeight);
        }

        if (iterationX > 0 && iterationY > 0) {
            for (int i = 0; i < iterationX; i++) {
                for (int j = 0; j < iterationY; j++) {
                    gui.blit(texture, x + (textureWidth * i), y + (textureHeight * j), edgeWidth, edgeWidth, textureWidth, textureHeight, totalSpriteWidth, totalSpriteHeight);
                }
            }

            if (remainingX > 0) {
                for (int i = 0; i < iterationY; i++)
                    gui.blit(texture, x + (textureWidth * iterationX), y + (textureHeight * i), edgeWidth, edgeWidth, remainingX, textureHeight, totalSpriteWidth, totalSpriteHeight);
            }

            if (remainingY > 0) {
                for (int i = 0; i < iterationX; i++)
                    gui.blit(texture, x + (textureWidth * i), y + (textureHeight * iterationY), edgeWidth, edgeWidth, textureWidth, remainingY, totalSpriteWidth, totalSpriteHeight);
            }

            if (remainingX > 0 && remainingY > 0) {
                gui.blit(texture, x + (textureWidth * iterationX), y + (textureHeight * iterationY), edgeWidth, edgeWidth, remainingX, remainingY, totalSpriteWidth, totalSpriteHeight);
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
