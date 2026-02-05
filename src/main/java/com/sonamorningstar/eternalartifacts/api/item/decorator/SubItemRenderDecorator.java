package com.sonamorningstar.eternalartifacts.api.item.decorator;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class SubItemRenderDecorator implements IItemDecorator {
	@Override
	public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
		if (stack.hasTag() && stack.getTag().getBoolean("Filled")) {
			CompoundTag cached = stack.getTag().getCompound("CachedResult");
			ItemStack result = ItemStack.of(cached);
			if (!result.isEmpty()) {
				PoseStack pose = guiGraphics.pose();
				pose.pushPose();
				pose.translate(0, 0, 100);
				pose.scale(0.5f, 0.5f, 0.5f);
				guiGraphics.renderItem(result, 2*(xOffset + 6), 2*(yOffset + 6), 42);
				pose.popPose();
				return true;
			}
		}
		return false;
	}
}
