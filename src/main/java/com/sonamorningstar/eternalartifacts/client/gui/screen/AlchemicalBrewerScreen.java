package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.AlchemicalBrewerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AlchemicalBrewerScreen extends AbstractSidedMachineScreen<AlchemicalBrewerMenu> {
	public AlchemicalBrewerScreen(AlchemicalBrewerMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyBar(gui);
		renderFluidBar(gui, leftPos + 24, topPos + 20, 0);
		renderFluidBar(gui, leftPos + imageWidth - 24, topPos + 20, 1);
		renderProgressArrowWTooltips(gui, leftPos + imageWidth - 80, topPos + 45, mx, my);
	}
}
