package com.sonamorningstar.eternalartifacts.api.client;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.render.FluidRendererHelper;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class ClientFiltersClampedTooltip implements ClientTooltipComponent {
	private final List<FilterEntry> entries;
	
	public record FiltersClampedTooltip(List<FilterEntry> entries) implements TooltipComponent {}
	
	public ClientFiltersClampedTooltip(FiltersClampedTooltip tooltip) {
		this.entries = tooltip.entries();
	}
	
	@Override
	public int getHeight() {
		return gridSizeY() * 18;
	}
	
	@Override
	public int getWidth(Font pFont) {
		return gridSizeX() * 18;
	}
	
	private int gridSizeX() {
		return Math.min(entries.size(), 3);
	}
	
	private int gridSizeY() {
		return (int) Math.ceil((float) entries.size() / 3);
	}
	
	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics gui) {
		for (int i = 0; i < entries.size(); i++) {
			int rX = x + 1 + (i % gridSizeX()) * 18;
			int rY = y + 1 + (i / gridSizeX()) * 18;
			FilterEntry entry = entries.get(i);
			if (entry instanceof ItemStackEntry itemStackEntry) {
				gui.renderItem(itemStackEntry.getFilterStack(), rX, rY);
				gui.renderItemDecorations(font, itemStackEntry.getFilterStack(), rX, rY);
			} else if (entry instanceof ItemTagEntry itemTagEntry) {
				ItemRendererHelper.renderItemCarousel(gui, Ingredient.of(itemTagEntry.getTag()).getItems(), rX, rY, 0x8054FFA3, 1.0F);
			} else if (entry instanceof FluidStackEntry fluidStackEntry) {
				FluidRendererHelper.renderFluidStack(gui, fluidStackEntry.getFilterStack(), rX, rY, 16, 16);
			} else if (entry instanceof FluidTagEntry fluidTagEntry) {
				FluidRendererHelper.renderFluidStackCarousel(gui, FluidIngredient.of(fluidTagEntry.getTag(), 1000).getFluidStacks(), rX, rY, 16, 16);
			}
		}
	}
}
