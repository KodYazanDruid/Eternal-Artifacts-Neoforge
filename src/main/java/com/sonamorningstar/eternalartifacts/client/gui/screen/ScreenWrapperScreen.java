package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.ScreenWrapperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenWrapperScreen extends AbstractModContainerScreen<ScreenWrapperMenu> {
    protected final Screen parent;
    public ScreenWrapperScreen(ScreenWrapperMenu menu, Inventory inv, Component title, Screen parent) {
        super(menu, inv, title);
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        parent.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return parent.mouseClicked(pMouseX, pMouseY, pButton);
    }
}
