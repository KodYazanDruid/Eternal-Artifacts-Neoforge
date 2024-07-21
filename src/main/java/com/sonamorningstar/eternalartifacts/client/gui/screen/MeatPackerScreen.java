package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.MeatPackerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MeatPackerScreen extends AbstractSidedMachineScreen<MeatPackerMenu> {
    public MeatPackerScreen(MeatPackerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mx, int my) {
        super.renderBg(gui, partialTick, mx, my);
        renderDefaultEnergyAndFluidBar(gui);
        renderProgressArrow(gui, x + 50, y + 35, mx, my);
    }
}
