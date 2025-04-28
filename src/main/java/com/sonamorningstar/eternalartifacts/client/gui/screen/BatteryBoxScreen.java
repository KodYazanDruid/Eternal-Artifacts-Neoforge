package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.BatteryBoxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BatteryBoxScreen extends AbstractSidedMachineScreen<BatteryBoxMenu> {
    public BatteryBoxScreen(BatteryBoxMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        setRedstoneControllable(false);
    }
    
    @Override
    protected void renderEnergyTooltip(GuiGraphics gui, int mx, int my) {
        renderEnergyTooltip(gui, mx, my, false);
    }
    
    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        super.renderBg(gui, partialTick, mouseX, mouseY);
        renderDefaultEnergyBar(gui);
    }
}
