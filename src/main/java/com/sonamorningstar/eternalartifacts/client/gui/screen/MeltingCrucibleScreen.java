package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MeltingCrucibleScreen extends AbstractSidedMachineScreen<GenericMachineMenu> {
    public MeltingCrucibleScreen(GenericMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
        super.renderBg(gui, tick, mx, my);
        renderDefaultEnergyBar(gui);
        renderFluidBar(gui, x + menu.arrowX + 42, y + 20);
        renderProgressArrow(gui, x + menu.arrowX, y + menu.arrowY, mx, my);
    }
}
