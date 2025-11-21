package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.DeepItemStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DeepItemStorageScreen extends AbstractModContainerScreen<DeepItemStorageMenu> {
	public DeepItemStorageScreen(DeepItemStorageMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float delta) {
		super.render(gui, mx, my, delta);
		renderTooltip(gui, mx, my);
	}
}
