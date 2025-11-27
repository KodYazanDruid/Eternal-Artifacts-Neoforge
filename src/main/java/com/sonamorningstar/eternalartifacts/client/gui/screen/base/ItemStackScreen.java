package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import com.mojang.blaze3d.platform.InputConstants;
import com.sonamorningstar.eternalartifacts.core.ModKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackScreen extends Screen {
    protected static Minecraft mc;
    protected final ItemStack stack;
    protected int leftPos;
    protected int topPos;
    protected int imageWidth;
    protected int imageHeight;
    public ItemStackScreen(ItemStack stack) {
        super(stack.getHoverName());
        this.stack = stack;
    }

    @Override
    protected void init() {
        mc = Minecraft.getInstance();
        leftPos = (width - imageWidth) / 2;
        topPos = (height - imageHeight) / 2;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partTick) {
        super.render(gui, mx, my, partTick);
        renderLabel(gui);
    }

    public void renderLabel(GuiGraphics gui) {
        gui.drawString(font, title, leftPos + 8, topPos + 6, 4210752, false);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 69) { // E key without modifiers
            if (getFocused() instanceof EditBox box && box.canConsumeInput()) return false; // Prevents closing the screen when typing in a text box
        }
        
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        else if (mc.options.keyInventory.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }else {
            return false;
        }
    }

}
