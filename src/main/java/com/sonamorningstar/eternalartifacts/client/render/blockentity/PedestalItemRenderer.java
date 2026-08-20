package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.content.block.entity.PedestalBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PedestalItemRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
	@Override
	public void render(PedestalBlockEntity pedestal, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (pedestal.inventory != null) {
			ItemStack stack = pedestal.inventory.getStackInSlot(0);
			if (!stack.isEmpty()) {
				poseStack.pushPose();
				poseStack.translate(0.5, 1.2, 0.5);
				poseStack.scale(0.5F, 0.5F, 0.5F);
				if (pedestal.hasLevel())
					poseStack.mulPose(Axis.YP.rotationDegrees((pedestal.getLevel().getGameTime() + partialTick) * 4));
				Minecraft.getInstance().getItemRenderer().render(
					stack, ItemDisplayContext.FIXED, false,
					poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
					Minecraft.getInstance().getItemRenderer().getModel(stack, pedestal.getLevel(), null, 0)
				);
				poseStack.popPose();
			}
		}
	}
}
