package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.AbstractBaseWidget;
import com.sonamorningstar.eternalartifacts.client.gui.widget.base.TooltipRenderable;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.MultiFurnace;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MultiFurnaceScreen<M extends AbstractMachineMenu> extends AbstractSidedMachineScreen<M> {
	public MultiFurnaceScreen(M menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	
	private final RecipeTypeButton[] recipeButtons = new RecipeTypeButton[4];
	
	@Override
	protected void init() {
		super.init();
		
		int buttonWidth = 16;
		int buttonHeight = 16;
		int startX = leftPos + imageWidth - buttonWidth - 55;
		int startY = topPos + 66;
		for (int i = 0; i < 4; i++) {
			final int recipeId = i;
			RecipeTypeButton button = new RecipeTypeButton(
				startX + (buttonWidth * i),
				startY,
				buttonWidth,
				buttonHeight,
				getRecipeItemStack(recipeId),
				getRecipeTooltip(recipeId),
				b -> {
					if (menu.getBlockEntity() instanceof MultiFurnace<?> furnace) {
						furnace.setRecipeTypeId((short) recipeId);
					}
					minecraft.gameMode.handleInventoryButtonClick(menu.containerId, recipeId);
					updateButtonStates();
				}
			);
			recipeButtons[i] = button;
			addRenderableWidget(button);
		}
		updateButtonStates();
	}
	
	private short getRecipeId() {
		if (menu.getBlockEntity() instanceof MultiFurnace<?> furnace) {
			return furnace.recipeTypeId;
		}
		return 0;
	}
	
	private Component getRecipeTooltip(int recipeId) {
		return switch (recipeId) {
			case 1 -> ModConstants.TOOLTIP.withSuffixTranslatable("recipe.blast_furnace");
			case 2 -> ModConstants.TOOLTIP.withSuffixTranslatable("recipe.smoker");
			case 3 -> ModConstants.TOOLTIP.withSuffixTranslatable("recipe.campfire");
			default -> ModConstants.TOOLTIP.withSuffixTranslatable("recipe.furnace");
		};
	}
	
	private void updateButtonStates() {
		for (int i = 0; i < 4; i++) {
			recipeButtons[i].setSelected(i == getRecipeId());
		}
	}
	
	@Override
	protected void drawExtraBg(GuiGraphics gui, float tickDelta, int x, int y) {
		drawRecipeType(gui);
		super.drawExtraBg(gui, tickDelta, x, y);
	}
	
	protected ItemStack getRecipeItemStack(int recipeId) {
		return switch (recipeId) {
			case 1 -> Items.BLAST_FURNACE.getDefaultInstance();
			case 2 -> Items.SMOKER.getDefaultInstance();
			case 3 -> Items.CAMPFIRE.getDefaultInstance();
			default -> Items.FURNACE.getDefaultInstance();
		};
	}
	
	protected void drawRecipeType(GuiGraphics gui) {
		if (menu.getBlockEntity() instanceof MultiFurnace<?> furnace) {
			ItemStack recipeItem = getRecipeItemStack(furnace.recipeTypeId);
			ItemRendererHelper.renderFakeItemTransparent(gui, recipeItem, leftPos + 25, topPos + 5, 96,
				5, 5, 5, 150);
		}
		gui.pose().translate(0, 0, 200);
	}
	
	@Override
	public void renderBackground(GuiGraphics gui, int mx, int my, float deltaTick) {
		PoseStack pose = gui.pose();
		pose.pushPose();
		pose.translate(0, 0, -200);
		super.renderBackground(gui, mx, my, deltaTick);
		pose.popPose();
		applyCustomGuiTint(gui, 0xa7ffa7a7);
		renderProgressArrowWTooltips(gui, leftPos + 81, topPos + 41, mx, my);
		resetGuiTint(gui);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyBar(gui);
	}
	
	private static class RecipeTypeButton extends AbstractBaseWidget implements TooltipRenderable {
		private static final ResourceLocation RECIPE_SELECTED_SPRITE = new ResourceLocation("container/stonecutter/recipe_selected");
		private static final ResourceLocation RECIPE_HIGHLIGHTED_SPRITE = new ResourceLocation("container/stonecutter/recipe_highlighted");
		private static final ResourceLocation RECIPE_SPRITE = new ResourceLocation("container/stonecutter/recipe");
		
		private final ItemStack recipeIcon;
		private final OnPress onPress;
		@Setter
		private boolean selected;
		
		public RecipeTypeButton(int x, int y, int width, int height, ItemStack recipeIcon, Component message, OnPress onPress) {
			super(x, y, width, height, message);
			this.recipeIcon = recipeIcon;
			this.onPress = onPress;
		}
		
		@Override
		public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
			isHovered = isMouseOver(mouseX, mouseY);
			ResourceLocation sprite = selected ? RECIPE_SELECTED_SPRITE
				: isHoveredOrFocused() ? RECIPE_HIGHLIGHTED_SPRITE : RECIPE_SPRITE;
			gui.blitSprite(sprite, getX(), getY(), width, height);
			ItemRendererHelper.renderFakeItemTransparent(gui, recipeIcon, getX(), getY(), 255);
		}
		
		@Override
		public void onClick(double mouseX, double mouseY, int button) {
			if (!selected) {
				onPress.onPress(this);
			}
		}
		
		@Override
		public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
			defaultButtonNarrationText(narrationElementOutput);
		}
		
		@Override
		public void renderTooltip(GuiGraphics gui, int mouseX, int mouseY, int tooltipZ) {
			if (isMouseOver(mouseX, mouseY)) {
				gui.pose().pushPose();
				gui.pose().translate(0, 0, tooltipZ);
				RenderSystem.disableDepthTest();
				gui.renderTooltip(Minecraft.getInstance().font, getMessage(), mouseX, mouseY);
				RenderSystem.enableDepthTest();
				gui.pose().popPose();
			}
		}
		
		public interface OnPress {
			void onPress(RecipeTypeButton button);
		}
	}
}
