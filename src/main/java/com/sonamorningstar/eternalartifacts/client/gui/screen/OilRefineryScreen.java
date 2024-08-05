package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.OilRefineryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class OilRefineryScreen extends AbstractSidedMachineScreen<OilRefineryMenu> {
    public OilRefineryScreen(OilRefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
        super.renderBg(gui, tick, mx, my);
        renderDefaultEnergyBar(gui);
        renderFluidBar(gui, x + 35, y + 20, 0);
        renderProgressArrow(gui, x + 63, y + 41, mx, my);
        renderFluidBar(gui, x + 95, y + 20, 1);
        renderFluidBar(gui, x + 115, y + 20, 2);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mx, int my) {
        gui.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}
