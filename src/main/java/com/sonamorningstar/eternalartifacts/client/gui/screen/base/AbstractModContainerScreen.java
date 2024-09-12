package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractModContainerScreen<T extends AbstractModContainerMenu> extends AbstractContainerScreen<T> {
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

    protected void setImageSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
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
        else GuiDrawer.drawBackground(gui, x, y, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        renderBackground(gui, mx, my, partialTick);
        super.render(gui, mx, my, partialTick);
        renderTooltip(gui, mx, my);
    }

    protected boolean isCursorInBounds(int startX, int startY, int lengthX, int lengthY, int mx, int my) {
        return mx >= startX && mx <= startX + lengthX &&
                my >= startY && my <= startY + lengthY;
    }

    protected void renderTankSlots(GuiGraphics gui, int x, int y) {
        for(FluidSlot slot : menu.fluidSlots) renderTankSlot(gui, x, y, slot);
    }

    protected void renderTankSlot(GuiGraphics gui, int x, int y, FluidSlot slot) {
        FluidStack stack = slot.getFluid();
        int percentage = stack.getAmount() * 12 / slot.getMaxSize();
        GuiDrawer.drawFluidWithSmallTank(gui, x + slot.x, y + slot.y, stack, percentage);
    }

    protected void renderSlots(GuiGraphics gui, int x, int y) {
        for(Slot slot : menu.slots) {
            gui.blitSprite(new ResourceLocation("container/slot"), x + slot.x-1, y + slot.y-1, 0, 18, 18);
        }
    }
}
