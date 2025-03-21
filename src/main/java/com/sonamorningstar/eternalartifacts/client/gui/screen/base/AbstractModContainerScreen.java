package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.DropdownMenu;
import com.sonamorningstar.eternalartifacts.client.gui.widget.Overlapping;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollablePanel;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public abstract class AbstractModContainerScreen<T extends AbstractModContainerMenu> extends EffectRenderingInventoryScreen<T> {
    //Margin: 5px
    //Corner: 5px * 5px
    //Sides: 5px * -px
    //Inside of Template: 166px * 156px
    //Total Size: 176px * 166px
    protected int x;
    protected int y;
    @Setter
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
    
    protected <G extends GuiEventListener & NarratableEntry> void addUpperLayerChild(G child) {
        addWidget(child);
        upperLayerChildren.add(child);
    }
    
    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        upperLayerChildren.clear();
    }
    
    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mX, int mY) {
        this.x = (width - imageWidth) / 2;
        this.y = (height - imageHeight) / 2;
        gui.setColor(FastColor.ARGB32.red(guiTint) / 255.0F, FastColor.ARGB32.green(guiTint) / 255.0F,
            FastColor.ARGB32.blue(guiTint) / 255.0F, FastColor.ARGB32.alpha(guiTint) / 255.0F);
        GuiDrawer.drawDefaultBackground(gui, x, y, imageWidth, imageHeight);
        gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        for(Slot slot : menu.slots) {
            gui.blitSprite(new ResourceLocation("container/slot"), x + slot.x-1, y + slot.y-1, 0, 18, 18);
        }
    }
    
    @Override
    protected void renderEffects(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (renderEffects) super.renderEffects(pGuiGraphics, pMouseX, pMouseY);
    }
    
    public static boolean isCursorInBounds(int startX, int startY, int lengthX, int lengthY, double mx, double my) {
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
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return super.mouseClicked(pMouseX, pMouseY, pButton);
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
            } else if (child instanceof ScrollablePanel<?> scrollPanel) {
                scrollPanel.updateHover(mx, my);
            }
        }
    }
}
