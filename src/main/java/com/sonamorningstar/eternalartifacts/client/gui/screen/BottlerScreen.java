package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.BottlerMenu;
import com.sonamorningstar.eternalartifacts.event.custom.RenderEtarSlotEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.common.NeoForge;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BottlerScreen extends AbstractSidedMachineScreen<BottlerMenu> {
	public BottlerScreen(BottlerMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyBar(gui);
		renderFluidBar(gui, leftPos + imageWidth - 24, topPos + 20);
	}
	
	@Override
	protected void renderSlotBg(GuiGraphics gui, Slot slot, ResourceLocation texture) {
		if (slot.index == 37) {
			int xPos = leftPos + slot.x - 5;
			int yPos = topPos + slot.y - 5;
			var event = NeoForge.EVENT_BUS.post(new RenderEtarSlotEvent(
				this, gui, slot, new ResourceLocation(MODID, "big_slot"), xPos, yPos, 0,26, 26, getGuiTint()));
			if (!event.isCanceled()) {
				applyCustomGuiTint(event.getGui(), event.getGuiTint());
				event.getGui().blitSprite(
					event.getTexture(), event.getX(), event.getY(), event.getBlitOffset(), event.getWidth(), event.getHeight()
				);
				resetGuiTint(event.getGui());
			}
		} else super.renderSlotBg(gui, slot, texture);
	}
}
