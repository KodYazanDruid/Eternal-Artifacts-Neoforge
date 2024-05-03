package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.container.AbstractMachineMenu;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AbstractMachineScreen<T extends AbstractMachineMenu> extends AbstractContainerScreen<T> {
    private static final ResourceLocation BARS = new ResourceLocation(MODID, "textures/gui/bars.png");
    @Nonnull
    @Setter
    protected static ResourceLocation texture;
    protected int x;
    protected int y;
    public AbstractMachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        this.x = (width - imageWidth) / 2;
        this.y = (height - imageHeight) / 2;
        pGuiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        inventoryLabelX = 46;
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    protected void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(BARS, x, y, 0, 0, 18, 56, 64, 64);
        guiGraphics.blit(BARS, x + 3, y + 53 - menu.getEnergyProgress(), 18, 53 - menu.getEnergyProgress(), 12, menu.getEnergyProgress(), 64, 64);
    }

    protected void renderFluidBar(GuiGraphics guiGraphics, int x, int y, FluidStack stack) {
        guiGraphics.blit(BARS, x, y, 30, 0, 18, 56, 64, 64);

        //FluidStack stack = anvilinatorBlockEntity.getFluidStack();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(stack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(stack);
        if(stillTexture == null) return;

        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

        int tintColor = fluidTypeExtensions.getTintColor(stack);
        float alpha = ((tintColor >> 24) & 0xFF) / 255f;
        float red = ((tintColor >> 16) & 0xFF) / 255f;
        float green = ((tintColor >> 8) & 0xFF) / 255f;
        float blue = ((tintColor) & 0xFF) / 255f;
        guiGraphics.setColor(red, green, blue, alpha);
        guiGraphics.blitTiledSprite(
                sprite,
                x + 3,
                y + 53 - menu.getFluidProgress(),
                0, //isn't this the ACTUAL offset wtf
                12,
                menu.getFluidProgress(),
                0, // these are offsets for atlas x
                0, //  y
                16, // Sprite dimensions to cut.
                16, //
                16, // Resolutions. 16x16 works fine.
                16);
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    protected void renderBurn(GuiGraphics pGuiGraphics, int x, int y) {
        if(menu.isWorking()) pGuiGraphics.blit(texture, x + 81, y + 55 + menu.getScaledProgress(), 176, menu.getScaledProgress(), 14, 14 - menu.getScaledProgress());
    }

}
