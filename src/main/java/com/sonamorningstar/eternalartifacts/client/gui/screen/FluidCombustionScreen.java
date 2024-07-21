package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.container.FluidCombustionMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidCombustionScreen extends AbstractMachineScreen<FluidCombustionMenu> {
    public FluidCombustionScreen(FluidCombustionMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float pPartialTick, int mx, int my) {
        super.renderBg(gui, pPartialTick, mx, my);
        renderDefaultEnergyAndFluidBar(gui);
        renderBurn(gui, x+81, y+55, mx, my);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
    }
}
