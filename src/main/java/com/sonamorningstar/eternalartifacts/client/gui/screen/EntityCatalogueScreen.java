package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.ItemStackScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.client.gui.widget.EntityCapsuleWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.ScrollBarWidget;
import com.sonamorningstar.eternalartifacts.client.render.EntityRendererHelper;
import com.sonamorningstar.eternalartifacts.content.item.EntityCatalogueItem;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SelectEntityMessageToServer;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityCatalogueScreen extends ItemStackScreen {
	private final List<LivingEntity> entities;
	private List<EntityCapsuleWidget> capsuleWidgets;
	private int selectedIndex = 0;
	private ScrollBarWidget scrollBar;
	public EntityCatalogueScreen(ItemStack stack) {
		super(stack);
		this.entities = EntityCatalogueItem.getSavedEntities(stack, Minecraft.getInstance().level);
	}
	
	@Override
	protected void init() {
		imageHeight = 192;
		imageWidth = 256;
		super.init();
		
		capsuleWidgets = new ArrayList<>();
		if (entities != null && !entities.isEmpty()) {
			scrollBar = new ScrollBarWidget(leftPos + imageWidth - 14, topPos + 18,
				8, imageHeight - 36, (entities.size() / 4 + 1) * 80);
			addRenderableWidget(scrollBar);
			int[] originalYs = new int[entities.size()];
			for (int i = 0; i < entities.size(); i++) {
				LivingEntity entity = entities.get(i);
				int cols = 4;
				int spacingX = 10;
				int spacingY = 10;
				int widgetWidth = 50;
				int widgetHeight = 70;
				int x = leftPos + 10 + (i % cols) * (widgetWidth + spacingX);
				int y = topPos + 18 + (i / cols) * (widgetHeight + spacingY);
				originalYs[i] = y;
				EntityCapsuleWidget capsule = new EntityCapsuleWidget(x, y, widgetWidth, widgetHeight,
					entity, 20f, true);
				addWidget(capsule);
				capsuleWidgets.add(capsule);
			}
			scrollBar.setOnScrolled(scroll -> {
				for (int i = 0; i < capsuleWidgets.size(); i++) {
					EntityCapsuleWidget capsule = capsuleWidgets.get(i);
					capsule.setY(originalYs[i] - (int) scroll);
				}
			});
			setFocused(capsuleWidgets.get(Math.min(selectedIndex, capsuleWidgets.size() - 1)));
		}
	}
	
	@Override
	public void renderBackground(GuiGraphics gui, int mx, int my, float partTick) {
		super.renderBackground(gui, mx, my, partTick);
		AbstractModContainerScreen.applyCustomGuiTint(gui, 0xFF1A4B2B);
		GuiDrawer.drawDefaultBackground(gui, leftPos, topPos, imageWidth, imageHeight);
		AbstractModContainerScreen.clearGuiTint(gui);
	}
	
	@Override
	public void renderLabel(GuiGraphics gui) {
		gui.drawString(font, title, leftPos + 8, topPos + 6, 0xFFFFFFFF, false);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partTick) {
		super.render(gui, mx, my, partTick);
		PoseStack pose = gui.pose();
		pose.pushPose();
		gui.enableScissor(leftPos + 10, topPos + 18, leftPos + imageWidth - 16, topPos + imageHeight - 24);
		for (EntityCapsuleWidget capsuleWidget : capsuleWidgets) {
			capsuleWidget.render(gui, mx, my, partTick);
		}
		gui.disableScissor();
		for (EntityCapsuleWidget capsuleWidget : capsuleWidgets) {
			var entity = capsuleWidget.getEntity();
			if (inScissorBox(mx, my) && capsuleWidget.isMouseOver(mx, my) && entity instanceof LivingEntity living)
				EntityRendererHelper.renderTooltip(gui, living, mx, my, mc.options.advancedItemTooltips);
		}
		pose.popPose();
		if (entities != null && !entities.isEmpty()) {
			gui.drawString(font, Component.translatable(ModConstants.GUI.withSuffix("catalogue.chosen_entity"), entities.get(selectedIndex).getDisplayName()),
				leftPos + 10, topPos + imageHeight - 18, 0xFFFFFFFF, false);
		}
	}
	
	private boolean inScissorBox(double mx, double my) {
		return mx >= leftPos + 10 && mx <= leftPos + imageWidth - 16 &&
			my >= topPos + 18 && my <= topPos + imageHeight - 24;
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (inScissorBox(mx, my)) {
			for (EntityCapsuleWidget capsuleWidget : capsuleWidgets) {
				if (capsuleWidget.mouseClicked(mx, my, button)) {
					setFocused(capsuleWidget);
					return true;
				}
			}
		}
		if (scrollBar == null) return super.mouseClicked(mx, my, button);
		setDragging(true);
		return scrollBar.mouseClicked(mx, my, button);
	}
	
	@Override
	public void setFocused(@Nullable GuiEventListener listener) {
		super.setFocused(listener);
		if (listener instanceof EntityCapsuleWidget capsule) {
			selectedIndex = capsuleWidgets.indexOf(capsule);
			Channel.sendToServer(new SelectEntityMessageToServer(selectedIndex));
		}
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
		if (scrollBar == null) return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
		return scrollBar.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY) || super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double pDragX, double pDragY) {
		if (scrollBar == null) return super.mouseDragged(mouseX, mouseY, button, pDragX, pDragY);
		return isDragging() && button == 0 ? scrollBar.mouseDragged(mouseX, mouseY, button, pDragX, pDragY)
			: super.mouseDragged(mouseX, mouseY, button, pDragX, pDragY);
	}
	
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		if (scrollBar == null) return super.mouseReleased(pMouseX, pMouseY, pButton);
		setDragging(false);
		return scrollBar.mouseReleased(pMouseX, pMouseY, pButton) || super.mouseReleased(pMouseX, pMouseY, pButton);
	}
}
