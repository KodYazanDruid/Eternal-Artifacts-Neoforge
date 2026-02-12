package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.container.EntityInteractorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EntityInteractorScreen extends AbstractMachineScreen<EntityInteractorMenu> {
	public EntityInteractorScreen(EntityInteractorMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyAndFluidBar(gui);
		renderProgressArrowWTooltips(gui, leftPos + 65, topPos + 45, mx, my);
	}
}
