package com.sonamorningstar.eternalartifacts.api.client;

import com.sonamorningstar.eternalartifacts.api.filter.*;
import com.sonamorningstar.eternalartifacts.client.render.FluidRendererHelper;
import com.sonamorningstar.eternalartifacts.client.render.ItemRendererHelper;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.FluidIngredient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.crafting.Ingredient;
import org.joml.Matrix4f;

public class ClientFilterTooltip implements ClientTooltipComponent {
	private final FilterEntry entry;
	private final FormattedCharSequence text;
	
	public record FilterTooltip(FilterEntry entry) implements TooltipComponent {}
	
	public ClientFilterTooltip(FilterTooltip tooltip) {
		this.entry = tooltip.entry();
		this.text = entry.getDisplayName().getVisualOrderText();
	}
	
	@Override
	public int getHeight() {
		return 18;
	}
	
	@Override
	public int getWidth(Font font) {
		return 20 + font.width(text);
	}
	
	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics gui) {
		int rX = x + 1;
		if (entry instanceof ItemStackEntry itemStackEntry) {
			gui.renderItem(itemStackEntry.getFilterStack(), rX, y);
			gui.renderItemDecorations(font, itemStackEntry.getFilterStack(), rX, y);
		} else if (entry instanceof ItemTagEntry itemTagEntry) {
			ItemRendererHelper.renderItemCarousel(gui, Ingredient.of(itemTagEntry.getTag()).getItems(), rX, y, 0x8054FFA3, 1.0F);
		} else if (entry instanceof FluidStackEntry fluidStackEntry) {
			FluidRendererHelper.renderFluidStack(gui, fluidStackEntry.getFilterStack(), rX, y, 16, 16);
		} else if (entry instanceof FluidTagEntry fluidTagEntry) {
			FluidRendererHelper.renderFluidStackCarousel(gui, FluidIngredient.of(fluidTagEntry.getTag(), 1000).getFluidStacks(), rX, y, 16, 16);
		}
	}
	
	@Override
	public void renderText(Font font, int mx, int my, Matrix4f matrix, MultiBufferSource.BufferSource buffer) {
		font.drawInBatch(this.text, (float)mx + 20, (float)my + 4, ChatFormatting.LIGHT_PURPLE.getColor(),
			true, matrix, buffer, Font.DisplayMode.NORMAL, 0, 15728880);
	}
}
