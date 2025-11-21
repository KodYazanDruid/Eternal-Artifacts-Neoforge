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
        renderFluidBar(gui, leftPos + 24, topPos + 20, 0);
        renderFluidBar(gui, leftPos + 44, topPos + 20, 1);
    }
    
    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
        renderBurn(gui, leftPos + 81, topPos + 55, mx, my);
    }
}
