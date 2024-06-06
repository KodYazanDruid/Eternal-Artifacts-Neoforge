package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
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
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        super.renderBg(gui, partialTick, mouseX, mouseY);
        renderDefaultEnergyAndFluidBar(gui);
        renderProgressArrow(gui, x + 50, y + 35);
    }
}
