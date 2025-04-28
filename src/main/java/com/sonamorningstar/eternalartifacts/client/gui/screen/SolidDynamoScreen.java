package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.container.SolidDynamoMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolidDynamoScreen extends AbstractDynamoScreen<SolidDynamoMenu> {
	public SolidDynamoScreen(SolidDynamoMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void renderBg(GuiGraphics gui, float pPartialTick, int mx, int my) {
		super.renderBg(gui, pPartialTick, mx, my);
		renderDefaultEnergyBar(gui);
		renderBurn(gui, leftPos + 81, topPos + 55, mx, my);
	}
}
