package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.FluidInfuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidInfuserScreen extends AbstractSidedMachineScreen<FluidInfuserMenu> {
    public FluidInfuserScreen(FluidInfuserMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
        super.renderBg(gui, tick, mx, my);
        renderDefaultEnergyAndFluidBar(gui);
        renderProgressArrow(gui, x + 83, y + 36, mx, my);
    }
}
