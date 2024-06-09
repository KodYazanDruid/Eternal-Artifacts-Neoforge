package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.MobLiquifierMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MobLiquifierScreen extends AbstractSidedMachineScreen<MobLiquifierMenu> {
    public MobLiquifierScreen(MobLiquifierMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(gui, pPartialTick, pMouseX, pMouseY);
        renderDefaultEnergyBar(gui);
        renderFluidBar(gui, x + 24, y + 20, 0);
        renderFluidBar(gui, x + 44, y + 20, 1);
        renderFluidBar(gui, x + 64, y + 20, 2);
        renderFluidBar(gui, x + 84, y + 20, 3);
    }
}
