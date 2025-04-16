package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SlotWidget extends AbstractWidget {
	private final Slot slot;
	public SlotWidget(Slot slot, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
		super(pX, pY, pWidth, pHeight, pMessage);
		this.slot = slot;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button) {
		if (slot instanceof FakeSlot fakeSlot) {
			ItemStack carried = slot.getItem();
			if (carried.isEmpty()) {
				ItemStack stack = slot.getItem();
				if (!stack.isEmpty()) {
					fakeSlot.set(ItemStack.EMPTY);
				}
			} else {
				slot.set(ItemStack.EMPTY);
			}
		}
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
		gui.pose().pushPose();
		gui.blitSprite(new ResourceLocation("container/slot"), getX() + slot.x-1, getY() + slot.y-1, 0, 18, 18);
		renderSlot(gui, slot);
		renderSlotHighlight(gui, slot, pMouseX, pMouseY, pPartialTick);
		gui.pose().popPose();
	}
	
	protected void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot) {
		int i = pSlot.x;
		int j = pSlot.y;
		ItemStack itemstack = pSlot.getItem();
		renderSlotContents(pGuiGraphics, itemstack, pSlot, i, j, ChatFormatting.YELLOW.toString() + itemstack.getCount());
	}
	
	protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, int x, int y, @Nullable String countString) {
		int j1 = slot.x + slot.y * 18;
		if (slot.isFake()) {
			guiGraphics.renderFakeItem(itemstack, x, y, j1);
		} else {
			guiGraphics.renderItem(itemstack, x, y, j1);
		}
		
		guiGraphics.renderItemDecorations(Minecraft.getInstance().font, itemstack, x, y, countString);
	}
	
	public static void renderSlotHighlight(GuiGraphics pGuiGraphics, int pX, int pY, int pBlitOffset) {
		renderSlotHighlight(pGuiGraphics, pX, pY, pBlitOffset, -2130706433);
	}
	public static void renderSlotHighlight(GuiGraphics pGuiGraphics, int pX, int pY, int pBlitOffset, int color) {
		pGuiGraphics.fillGradient(RenderType.guiOverlay(), pX, pY, pX + 16, pY + 16, color, color, pBlitOffset);
	}
	
	protected void renderSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick) {
		if (slot.isHighlightable()) {
			renderSlotHighlight(guiGraphics, slot.x, slot.y, 0, -2130706433);
		}
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
	
	}
}
