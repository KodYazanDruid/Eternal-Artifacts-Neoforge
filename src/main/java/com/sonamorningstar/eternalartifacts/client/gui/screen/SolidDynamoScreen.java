package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractMachineScreen;
import com.sonamorningstar.eternalartifacts.container.SolidDynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.SolidCombustionDynamo;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolidDynamoScreen extends AbstractMachineScreen<SolidDynamoMenu> {
	public SolidDynamoScreen(SolidDynamoMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void renderBg(GuiGraphics gui, float pPartialTick, int mx, int my) {
		super.renderBg(gui, pPartialTick, mx, my);
		renderDefaultEnergyBar(gui);
		renderBurn(gui, leftPos + 81, topPos + 55, mx, my);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		if (menu.getBlockEntity() instanceof SolidCombustionDynamo dynamo){
			DynamoProcessCache cache = dynamo.getCache();
			if (cache != null){
				gui.drawString(font,
					ModConstants.GUI.withSuffixTranslatable("dynamo_produce_rate")
						.append(": " + cache.getGeneration()+"RF/T"),
					leftPos + 44, topPos + 46, labelColor, false);
			}
		}
	}
}
