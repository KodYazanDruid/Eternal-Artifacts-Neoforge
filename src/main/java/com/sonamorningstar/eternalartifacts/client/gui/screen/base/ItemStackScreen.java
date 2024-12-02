package com.sonamorningstar.eternalartifacts.client.gui.screen.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackScreen extends Screen {
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
}
