package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.PortableFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class PortableFurnaceScreen extends AbstractModContainerScreen<PortableFurnaceMenu> {
	private static final ResourceLocation LIT_PROGRESS_SPRITE = new ResourceLocation("container/furnace/lit_progress");
	private static final ResourceLocation BURN_PROGRESS_SPRITE = new ResourceLocation("container/furnace/burn_progress");
	private static final ResourceLocation FURNACE_GUI = new ResourceLocation("textures/gui/container/furnace.png");
	
	public PortableFurnaceScreen(PortableFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		this.inventoryLabelY = this.imageHeight - 92;
	}
	
	@Override
	protected void renderBg(GuiGraphics gui, float tickDelta, int mX, int mY) {
		super.renderBg(gui, tickDelta, mX, mY);
		gui.blit(FURNACE_GUI, leftPos + 57, topPos + 37, 57, 37, 13, 13);
		gui.blit(FURNACE_GUI, leftPos + 80, topPos + 35, 80, 35, 22, 15);
		if (menu.hasFuel()) {
			int l = Mth.ceil(menu.getFuelProgress() * 13.0F) + 1;
			gui.blitSprite(LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - l, leftPos + 56, topPos + 36 + 14 - l, 14, l);
		}
		
		int j1 = Mth.ceil(menu.getRecipeProgress() * 24.0F);
		gui.blitSprite(BURN_PROGRESS_SPRITE, 24, 16, 0, 0, leftPos + 79, topPos + 34, j1, 16);
		
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float delta) {
		super.render(gui, mx, my, delta);
		renderTooltip(gui, mx, my);
	}
}
