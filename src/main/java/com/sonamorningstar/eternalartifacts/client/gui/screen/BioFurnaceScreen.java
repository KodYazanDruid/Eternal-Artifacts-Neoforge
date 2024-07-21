package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.container.BioFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BioFurnaceScreen extends AbstractMachineScreen<BioFurnaceMenu> {
    public BioFurnaceScreen(BioFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float pPartialTick, int mx, int my) {
        super.renderBg(gui, pPartialTick, mx, my);
        renderDefaultEnergyBar(gui);
        renderBurn(gui, x + 81, y + 55, mx, my);
        renderFluidBar(gui, x + 24, y + 20, 0);
        renderFluidBar(gui, x + 44, y + 20, 1);
    }

}
