package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.container.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends AbstractMachineScreen<SolarPanelMenu> {
	public SolarPanelScreen(SolarPanelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void renderBg(GuiGraphics gui, float tickDelta, int mX, int mY) {
		super.renderBg(gui, tickDelta, mX, mY);
		renderDefaultEnergyBar(gui);
	}
}
