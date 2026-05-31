package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.AdvancedCrafterMenu;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class AdvancedCrafterScreen extends AbstractSidedMachineScreen<AdvancedCrafterMenu> {
	public AdvancedCrafterScreen(AdvancedCrafterMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}
	
	@Override
	protected void renderBg(GuiGraphics gui, float tick, int mx, int my) {
		super.renderBg(gui, tick, mx, my);
		renderDefaultEnergyAndFluidBar(gui);
		renderProgressArrowWTooltips(gui, leftPos + 99, topPos + 37, mx, my);
	}
	
	@Override
	protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, int x, int y, @Nullable String countString) {
		if (slot instanceof SlotItemHandler sih) {
			if (itemstack.isEmpty()) {
				var inv = menu.getBeInventory();
				if (inv != null) {
					var blueprint = inv.getStackInSlot(10);
					var index = sih.getSlotIndex();
					if (index >= 0 && index < 9 && !blueprint.isEmpty() && blueprint.getItem() instanceof BlueprintItem) {
						var pattern = BlueprintItem.getPattern(blueprint);
						if (BlueprintItem.isUsingTags(blueprint)) {
							CraftingRecipe recipe = pattern.getRecipe();
							if (recipe != null) {
								var ingredient = recipe.getIngredients().get(index);
								if (!ingredient.isEmpty()) {
									ItemRendererHelper.renderItemCarousel(guiGraphics, ingredient.getItems(), x, y, 0.375F);
									return;
								}
							}
							return;
						} else {
							var items = pattern.getFakeItems().getItems();
							if (index < items.size()) {
								ItemRendererHelper.renderFakeItemTransparent(guiGraphics, items.get(index), x, y, 96);
								return;
							}
						}
					}
				}
			}
		}
		super.renderSlotContents(guiGraphics, itemstack, slot, x, y, countString);
	}
}
