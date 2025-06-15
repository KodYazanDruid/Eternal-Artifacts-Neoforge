package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.Overlapping;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import com.sonamorningstar.eternalartifacts.event.custom.RenderEtarSlotEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractModContainerScreen<T extends AbstractModContainerMenu> extends EffectRenderingInventoryScreen<T> {
    //Margin: 5px
    //Corner: 5px * 5px
    //Sides: 5px * -px
    //Inside of Template: 166px * 156px
    //Total Size: 176px * 166px
    @Setter
    @Getter
    private int guiTint = 0xFFFFFFFF;
    protected boolean renderEffects = true;
    public final List<GuiEventListener> upperLayerChildren = new ArrayList<>();

    public AbstractModContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    protected void setImageSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
    }
    
    public <G extends GuiEventListener & NarratableEntry> void addUpperLayerChild(G child) {
        children.add(0, child);
        narratables.add(0, child);
        upperLayerChildren.add(child);
    }
    
    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        upperLayerChildren.clear();
    }
    
    protected void drawExtraBg(GuiGraphics gui, float tickDelta, int x, int y) {
        applyGuiTint(gui);
        renderSlots(gui);
        clearGuiTint(gui);
    }
    
    protected void renderSlots(GuiGraphics gui) {
        for(Slot slot : menu.slots) {
            renderSlot(gui, slot, new ResourceLocation("container/slot"));
        }
    }
    
    protected void renderSlot(GuiGraphics gui, Slot slot, ResourceLocation texture) {
        int xPos = leftPos + slot.x - 1;
        int yPos = topPos + slot.y - 1;
        var event = NeoForge.EVENT_BUS.post(new RenderEtarSlotEvent(
            this, gui, slot, texture, xPos, yPos, 0,18, 18, guiTint));
        if (!event.isCanceled()) {
            applyCustomGuiTint(event.getGui(), event.getGuiTint());
            event.getGui().blitSprite(
                event.getTexture(), event.getX(), event.getY(), event.getBlitOffset(), event.getWidth(), event.getHeight()
            );
            resetGuiTint(event.getGui());
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics gui, float tickDelta, int mX, int mY) {
        applyGuiTint(gui);
        GuiDrawer.drawDefaultBackground(gui, leftPos, topPos, imageWidth, imageHeight);
        drawExtraBg(gui, tickDelta, leftPos, topPos);
        clearGuiTint(gui);
    }
    
    public void applyGuiTint(GuiGraphics gui) {
        gui.setColor(FastColor.ARGB32.red(guiTint) / 255.0F, FastColor.ARGB32.green(guiTint) / 255.0F,
            FastColor.ARGB32.blue(guiTint) / 255.0F, FastColor.ARGB32.alpha(guiTint) / 255.0F);
    }
    public void applyGuiTint(GuiGraphics gui, int alpha) {
        gui.setColor(FastColor.ARGB32.red(guiTint) / 255.0F, FastColor.ARGB32.green(guiTint) / 255.0F,
            FastColor.ARGB32.blue(guiTint) / 255.0F, alpha / 255.0F);
    }
    public void applyCustomGuiTint(GuiGraphics gui, int color) {
        gui.setColor(FastColor.ARGB32.red(color) / 255.0F, FastColor.ARGB32.green(color) / 255.0F,
            FastColor.ARGB32.blue(color) / 255.0F, FastColor.ARGB32.alpha(color) / 255.0F);
    }
    public void resetGuiTint(GuiGraphics gui) {
        applyGuiTint(gui);
    }
    public void clearGuiTint(GuiGraphics gui) {
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    @Override
    protected void renderEffects(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (renderEffects) super.renderEffects(pGuiGraphics, pMouseX, pMouseY);
    }
    
    public boolean isCursorInBounds(int startX, int startY, int lengthX, int lengthY, double mx, double my) {
        Optional<GuiEventListener> child = getChildAt(mx, my);
        if (child.isPresent()) {
            if (child.get() instanceof Overlapping) {
                return false;
            }
        }
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
    
    @Override
    public boolean mouseDragged(double mx, double my, int button, double dragX, double dragY) {
        super.mouseDragged(mx, my, button, dragX, dragY);
        return getFocused() != null && isDragging() && button == 0 && getFocused().mouseDragged(mx, my, button, dragX, dragY);
    }
    
    @Override
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        Optional<GuiEventListener> child = getChildAt(pMouseX, pMouseY);
        if (child.isPresent()) {
            if (child.get() instanceof Overlapping) {
                return false;
            }
        }
        return super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }
    
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        boolean ret = super.mouseClicked(mx, my, button);
        Optional<GuiEventListener> child = getChildAt(mx, my);
        if (child.isPresent()) {
            if (child.get() instanceof Overlapping overlapping) {
                GuiEventListener listener = overlapping.getElementUnderMouse(mx, my);
                if (listener != null) setFocused(listener);
            }
        }
        return ret;
    }
    
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 69) { // E key without modifiers
            GuiEventListener focused = getFocused();
            if (focused instanceof EditBox box && box.canConsumeInput()) return false; // Prevents closing the screen when typing in a text box
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    
    @Override
    public void render(GuiGraphics gui, int mx, int my, float delta) {
        super.render(gui, mx, my, delta);
        boolean foundOpenMenu = false;
        for (GuiEventListener child : children) {
            if (child instanceof AbstractWidget widget &&
                widget instanceof Overlapping overlapping) {
                if (!foundOpenMenu && widget.isMouseOver(mx, my)) {
                    overlapping.updateHover(mx, my);
                    foundOpenMenu = true;
                } else {
                    overlapping.updateHover(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
                }
            }
        }
    }
}
