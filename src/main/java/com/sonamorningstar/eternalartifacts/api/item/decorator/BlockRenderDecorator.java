package com.sonamorningstar.eternalartifacts.api.item.decorator;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.IItemDecorator;

import java.util.function.Function;

public class BlockRenderDecorator implements IItemDecorator {
	private final Function<ItemStack, BlockEntity> blockEntityProvider;
	
	public BlockRenderDecorator(Function<ItemStack, BlockEntity> blockEntityProvider) {
		this.blockEntityProvider = blockEntityProvider;
	}
	
	@Override
	public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
		BlockEntity be = blockEntityProvider.apply(stack);
		if (be != null) {
			PoseStack pose = guiGraphics.pose();
			pose.pushPose();
			pose.translate(0, 0, 100);
			pose.scale(0.5f, 0.5f, 0.5f);
			Minecraft mc = Minecraft.getInstance();
			BlockEntityRenderDispatcher renderer = mc.getBlockEntityRenderDispatcher();
			renderer.render(be, mc.getPartialTick(), pose, guiGraphics.bufferSource());
			pose.popPose();
			return true;
		}
		return false;
	}
}
