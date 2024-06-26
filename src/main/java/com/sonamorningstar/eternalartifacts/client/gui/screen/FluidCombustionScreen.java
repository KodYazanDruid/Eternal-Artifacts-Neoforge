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
    protected void renderBg(GuiGraphics gui, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(gui, pPartialTick, pMouseX, pMouseY);
        renderDefaultEnergyAndFluidBar(gui);
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float partialTick) {
        super.render(gui, mx, my, partialTick);
    }
}
