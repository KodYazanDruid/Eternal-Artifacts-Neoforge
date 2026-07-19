package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.container.FluidFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidFurnaceScreen extends AbstractMachineScreen<FluidFurnaceMenu> {
	public FluidFurnaceScreen(FluidFurnaceMenu menu, Inventory pPlayerInventory, Component pTitle) {
		super(menu, pPlayerInventory, pTitle);
		setGuiTint(0xFFD68963);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float delta) {
		super.render(gui, mx, my, delta);
		renderTooltip(gui, mx, my);
		renderBurn(gui, leftPos + 57, topPos + 36, mx, my);
		renderProgressArrowWTooltips(gui, leftPos + 79, topPos + 34, mx, my);
	}
	
	@Override
	protected void renderBurn(GuiGraphics guiGraphics, int x, int y, int mx, int my) {
		guiGraphics.blit(bars, x + 1, y + 1, 48, 10, 13, 13);
		int burnProgress = menu.data.get(3) != 0 && menu.data.get(2) != 0 ? menu.data.get(2) * 14 / menu.data.get(3) : 0;
		guiGraphics.blit(bars, x, y + 14 - burnProgress, 48,  37 - burnProgress, 14, burnProgress);
		renderProgressTooltip(guiGraphics, x, y, 13, 13, mx, my, menu.data.get(2), menu.data.get(3), "burn_time");
	}
}
