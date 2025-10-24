package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.HarvesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HarvesterScreen extends AbstractSidedMachineScreen<HarvesterMenu> {
	public HarvesterScreen(HarvesterMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyBar(gui);
		renderDefaultFluidBar(gui);
	}
}
