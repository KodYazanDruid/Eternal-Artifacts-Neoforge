package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.AutoCutterMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.AutoCutter;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class AutoCutterScreen extends AbstractSidedMachineScreen<AutoCutterMenu> {
	private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("container/stonecutter/scroller");
	private static final ResourceLocation SCROLLER_DISABLED_SPRITE = new ResourceLocation("container/stonecutter/scroller_disabled");
	private static final ResourceLocation RECIPE_SELECTED_SPRITE = new ResourceLocation("container/stonecutter/recipe_selected");
	private static final ResourceLocation RECIPE_HIGHLIGHTED_SPRITE = new ResourceLocation("container/stonecutter/recipe_highlighted");
	private static final ResourceLocation RECIPE_SPRITE = new ResourceLocation("container/stonecutter/recipe");
	private static final ResourceLocation STONECUTTER_BG = new ResourceLocation("textures/gui/container/stonecutter.png");
	private float scrollOffs;
	private boolean scrolling;
	private int startIndex = 0;
	public AutoCutterScreen(AutoCutterMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		menu.inputListeners.add(this::containerChanged);
	}
	
	@Override
	protected void init() {
		super.init();
		addRenderableWidget(Button.builder(Component.empty(), b -> {
			if (menu.getBlockEntity() instanceof AutoCutter autoCutter) {
				autoCutter.setSelectedRecipeIndex(-1);
				minecraft.gameMode.handleInventoryButtonClick(menu.containerId, -1);
			}
		}).bounds(leftPos + 26, topPos + 20, 16, 18)
			.tooltip(Tooltip.create(ModConstants.GUI.withSuffixTranslatable("autocutter.reset_index"))).build());
	}
	
	@Override
	protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
		super.renderBg(gui, tick, mx, my);
		renderProgressArrow(gui, leftPos + 118, topPos + 41, mx, my);
		renderRecipes(gui, leftPos + 46, topPos + 20, mx, my);
		int k = (int)(39.0F * this.scrollOffs);
		ResourceLocation resourcelocation = this.isScrollBarActive() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
		gui.blitSprite(resourcelocation, leftPos + 113, topPos + 20 + k, 12, 15);
	}
	
	private void renderRecipes(GuiGraphics gui, int x, int y, int mx, int my) {
		if (menu.getBlockEntity() instanceof AutoCutter autoCutter) {
			var recipes = autoCutter.getRecipes();
			gui.blit(STONECUTTER_BG, x - 1, y - 1, 51, 14, 66, 56);
			for(int i = this.startIndex; i < (startIndex + 12) && i < recipes.size(); ++i) {
				int j = i - this.startIndex;
				int pX = x + (j % 4) * 16;
				int pY = y + (j / 4) * 18;
				ResourceLocation sprite;
				if (i == menu.getSelectedRecipeIndex()) sprite = RECIPE_SELECTED_SPRITE;
				else if (isCursorInBounds(pX, pY, 16, 18, mx, my)) sprite = RECIPE_HIGHLIGHTED_SPRITE;
				else sprite = RECIPE_SPRITE;
				gui.blitSprite(sprite, pX, pY, 16, 18);
				gui.renderItem(recipes.get(i).value().getResultItem(minecraft.level.registryAccess()), pX, pY + 2);
			}
			
		}
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyBar(gui);
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
	}
	
	private void containerChanged() {
		var inv = getMenu().getBeInventory();
		if (inv != null && inv.getStackInSlot(0).isEmpty()) {
			this.startIndex = 0;
			this.scrollOffs = 0.0F;
		}
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		scrolling = false;
		if (menu.getBlockEntity() instanceof AutoCutter autoCutter) {
			var recipes = autoCutter.getRecipes();
			for(int i = this.startIndex; i < (startIndex + 12) && i < recipes.size(); ++i) {
				int j = i - this.startIndex;
				int pX = (leftPos + 46) + (j % 4) * 16;
				int pY = (topPos + 20) + (j / 4) * 18;
				if (isCursorInBounds(pX, pY, 16, 18, mx, my)) {
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
					autoCutter.setSelectedRecipeIndex(i);
					minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
					return true;
				}
			}
		}
		int i = this.leftPos + 113;
		int j = this.topPos + 20;
		if (mx >= (double)i && mx < (double)(i + 12) && my >= (double)j && my < (double)(j + 56)) {
			this.scrolling = true;
		}
		return super.mouseClicked(mx, my, button);
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		if (this.scrolling && this.isScrollBarActive()) {
			int i = this.topPos + 19;
			int j = i + 54;
			this.scrollOffs = ((float)pMouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
			this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
			this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5) * 4;
			return true;
		} else {
			return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
		}
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
		if (this.isScrollBarActive()) {
			int i = this.getOffscreenRows();
			float f = (float)pScrollY / (float)i;
			this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
			this.startIndex = (int)((double)(this.scrollOffs * (float)i) + 0.5) * 4;
		}
		
		return true;
	}
	
	private boolean isScrollBarActive() {
		return this.menu.getRecipes().size() > 12;
	}
	
	protected int getOffscreenRows() {
		return (this.menu.getRecipes().size() + 4 - 1) / 4 - 3;
	}
}
