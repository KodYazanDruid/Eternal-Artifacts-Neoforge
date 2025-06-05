package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public final class FluidRendererHelper {
	private static final Minecraft M = Minecraft.getInstance();
	
	public static boolean renderFluidStack(GuiGraphics gui, FluidStack stack, int x, int y, int width, int height) {
		if (stack.isEmpty()) return false;
		IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(stack.getFluid());
		ResourceLocation still = fluidTypeExtensions.getStillTexture(stack);
		if (still == null) return false;
		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(still);
		int tintColor = fluidTypeExtensions.getTintColor(stack);
		float alpha = ((tintColor >> 24) & 0xFF) / 255f;
		float red = ((tintColor >> 16) & 0xFF) / 255f;
		float green = ((tintColor >> 8) & 0xFF) / 255f;
		float blue = ((tintColor) & 0xFF) / 255f;
		gui.setColor(red, green, blue, alpha);
		gui.blitSprite(sprite, x, y, 0, width, height);
		gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		return true;
	}
	
	public static boolean renderFluidStackCarousel(GuiGraphics gui, FluidStack[] stacks, int x, int y, int width, int height) {
		long tick = M.clientTickCount;
		if (stacks.length == 0) return false;
		FluidStack fluidStack = stacks[(int) ((tick / 20) % stacks.length)];
		renderFluidStack(gui, fluidStack, x, y, width, height);
		return true;
	}
}
