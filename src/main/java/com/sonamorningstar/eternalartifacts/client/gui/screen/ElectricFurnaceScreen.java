package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.ElectricFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.ElectricFurnace;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class ElectricFurnaceScreen extends AbstractSidedMachineScreen<ElectricFurnaceMenu> {
	public ElectricFurnaceScreen(ElectricFurnaceMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	
	@Override
	protected void init() {
		super.init();
		int currValue = menu.getBlockEntity() instanceof ElectricFurnace furnace ? furnace.recipeTypeId : 0;
		addRenderableWidget(new ExtendedSlider(leftPos + 100, topPos + 10, 50, 10, Component.empty(), Component.empty(),
			0, 3, currValue, 1, 1, true) {
			@Override
			protected void applyValue() {
				if (menu.getBlockEntity() instanceof ElectricFurnace furnace) {
					furnace.setRecipeTypeId((short) getValueInt());
				}
				minecraft.gameMode.handleInventoryButtonClick(menu.containerId, getValueInt());
			}
		});
	}
	
	@Override
	protected void drawExtraBg(GuiGraphics gui, float tickDelta, int x, int y) {
		if (menu.getBlockEntity() instanceof ElectricFurnace furnace) {
			ItemStack recipeItem;
			switch (furnace.recipeTypeId) {
				case 1 -> recipeItem = Items.BLAST_FURNACE.getDefaultInstance();
				case 2 -> recipeItem = Items.SMOKER.getDefaultInstance();
				case 3 -> recipeItem = Items.CAMPFIRE.getDefaultInstance();
				default -> recipeItem = Items.FURNACE.getDefaultInstance();
			}
			ItemRendererHelper.renderFakeItemTransparent(gui, recipeItem, leftPos + 25, topPos + 5, 96,
				5, 5, 5, 150);
		}
		gui.pose().translate(0, 0, 200);
		super.drawExtraBg(gui, tickDelta, x, y);
	}
	
	@Override
	public void renderBackground(GuiGraphics gui, int mx, int my, float deltaTick) {
		PoseStack pose = gui.pose();
		pose.pushPose();
		pose.translate(0, 0, -200);
		super.renderBackground(gui, mx, my, deltaTick);
		pose.popPose();
		renderProgressArrow(gui, leftPos + 81, topPos + 41, mx, my);
	}
	
	@Override
	public void render(GuiGraphics gui, int mx, int my, float partialTick) {
		super.render(gui, mx, my, partialTick);
		renderDefaultEnergyBar(gui);
	}
}
