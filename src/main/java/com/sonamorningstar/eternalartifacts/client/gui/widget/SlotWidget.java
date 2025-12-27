package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

@Getter
public class SlotWidget extends AbstractWidget implements TooltipRenderable {
	private final Slot slot;
	public SlotWidget(Slot slot, Component pMessage) {
		super(slot.x, slot.y, 18, 18, pMessage);
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
	public void playDownSound(SoundManager pHandler) {}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mx, int my, float pPartialTick) {
		if (slot.isActive()) {
			gui.pose().pushPose();
			gui.blitSprite(new ResourceLocation("container/slot"), getX() - 1, getY() - 1, 0, 18, 18);
			renderSlot(gui, slot, mx, my);
			gui.pose().popPose();
		}
	}
	
	@Override
	public void renderTooltip(GuiGraphics gui, int mouseX, int mouseY, int tooltipZ) {
		if (slot.isActive() && isMouseOver(mouseX, mouseY)) {
			ItemStack itemstack = slot.getItem();
			if (!itemstack.isEmpty()) {
				gui.pose().pushPose();
				gui.pose().translate(0, 0, tooltipZ);
				RenderSystem.disableDepthTest();
				gui.renderTooltip(Minecraft.getInstance().font, itemstack, mouseX, mouseY);
				RenderSystem.enableDepthTest();
				gui.pose().popPose();
			}
		}
	}
	
	protected void renderSlot(GuiGraphics gui, Slot pSlot, int mouseX, int mouseY) {
		ItemStack itemstack = pSlot.getItem();
		renderSlotContents(gui, itemstack, pSlot, getX(), getY(), ChatFormatting.WHITE.toString() + itemstack.getCount());
		renderSlotHighlight(gui, pSlot, mouseX, mouseY, 0);
	}
	
	protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, int x, int y, @Nullable String countString) {
		if (itemstack.isEmpty()) return;
		
		int seed = 0;
		if (slot.isFake()) {
			guiGraphics.renderFakeItem(itemstack, x, y, seed);
		} else {
			guiGraphics.renderItem(itemstack, x, y, seed);
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
		if (slot.isHighlightable() && isMouseOver(mouseX, mouseY)) {
			renderSlotHighlight(guiGraphics, getX(), getY(), 0, -2130706433);
		}
	}
	
	@Override
	public boolean isMouseOver(double mX, double mY) {
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof AbstractModContainerScreen<?> modScreen) {
			for (int i = modScreen.upperLayerChildren.size() - 1; i >= 0; i--) {
				GuiEventListener child = modScreen.upperLayerChildren.get(i);
				if (child instanceof ParentalWidget parental) {
					if (child instanceof SimpleDraggablePanel panel && panel.isMouseOverRaw(mX, mY)) {
						return parental.getChildUnderCursorRaw(mX, mY) == this;
					} else return super.isMouseOver(mX, mY);
				}
			}
		}
		return super.isMouseOver(mX, mY);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
	
	}
}
