package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.BasicAttachmentMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BasicAttachmentScreen extends AbstractModContainerScreen<BasicAttachmentMenu> {
	
	public BasicAttachmentScreen(BasicAttachmentMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		renderEffects = false;
		setGuiTint(0xffe5ea4d);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float delta) {
		super.render(gui, mx, my, delta);
		renderTooltip(gui, mx, my);
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
		pGuiGraphics.drawString(font, Component.literal(menu.getDir().toString()), 50, 6, 0xFF404040);
	}
	
	@Override
	protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
		super.slotClicked(slot, slotId, mouseButton, type);
		if (slot instanceof FakeSlot fakeSlot && !fakeSlot.isDisplayOnly()) {
			ItemStack carried = menu.getCarried();
			if (carried.isEmpty()) {
				ItemStack stack = slot.getItem();
				if (!stack.isEmpty()) {
					fakeSlot.set(ItemStack.EMPTY);
					updateItem(menu.containerId, fakeSlot.getSlotIndex(), ItemStack.EMPTY);
				}
			} else {
				ItemStack stack = carried.copyWithCount(1);
				fakeSlot.set(stack);
				updateItem(menu.containerId, fakeSlot.getSlotIndex(), stack);
			}
		}
	}
	
	private void updateItem(int menuId, int slotIndex, ItemStack stack) {
		Channel.sendToServer(new UpdateFakeSlotToServer(menuId, slotIndex, stack));
	}
}
