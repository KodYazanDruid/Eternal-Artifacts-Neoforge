package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.AbstractBaseWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.ParentalWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.TooltipRenderable;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

@Getter
public class SlotWidget extends AbstractBaseWidget implements TooltipRenderable {
	private final Slot slot;
	
	public SlotWidget(Slot slot) {
		super(slot.x, slot.y, 18, 18, Component.empty());
		this.slot = slot;
	}
	
	public void registerToScreen(AbstractModContainerScreen<?> screen) {
		screen.registerWidgetManagedSlot(slot);
	}
	
	public void unregisterFromScreen(AbstractModContainerScreen<?> screen) {
		screen.unregisterWidgetManagedSlot(slot);
	}
	
	@Override
	protected boolean isValidClickButton(int button) {
		return button == 0 || button == 1;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button) {
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.screen instanceof AbstractContainerScreen<?> containerScreen)) return;
		AbstractContainerMenu menu = containerScreen.getMenu();
		
		if (slot instanceof FakeSlot fakeSlot) {
			ItemStack carried = menu.getCarried().copyWithCount(1);
			if (carried.isEmpty()) fakeSlot.set(ItemStack.EMPTY);
			else fakeSlot.set(carried);
		} else {
			// Menüdeki slot listesinden doğru indeksi bul
			int menuSlotIndex = findSlotIndexInMenu(menu);
			if (menuSlotIndex == -1) return; // Slot menüde değilse işlem yapma
			
			ClickType clickType = ClickType.PICKUP;
			
			// Shift tıklama kontrolü
			if (Screen.hasShiftDown()) {
				clickType = ClickType.QUICK_MOVE;
			}
			
			// Slot üzerinde slotClicked çağrısı yap - bu sunucuya paket gönderir
			if (mc.gameMode != null && mc.player != null) {
				mc.gameMode.handleInventoryMouseClick(menu.containerId, menuSlotIndex, button, clickType, mc.player);
			}
		}
	}
	
	/**
	 * Menüdeki slot listesinden bu slotun indeksini bulur.
	 * @return Slot indeksi, bulunamazsa -1
	 */
	private int findSlotIndexInMenu(AbstractContainerMenu menu) {
		for (int i = 0; i < menu.slots.size(); i++) {
			if (menu.slots.get(i) == slot) {
				return i;
			}
		}
		return -1;
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
		renderSlotContents(gui, itemstack, pSlot, getX(), getY(), null);
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
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}
}
