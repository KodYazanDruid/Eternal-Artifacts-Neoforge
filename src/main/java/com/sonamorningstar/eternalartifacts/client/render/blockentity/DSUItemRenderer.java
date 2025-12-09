package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.render.util.RendererHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.DeepItemStorageUnit;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class DSUItemRenderer implements BlockEntityRenderer<DeepItemStorageUnit> {
	@Override
	public void render(DeepItemStorageUnit dsu, float delta, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
		if (!dsu.hasLevel()) {
			Minecraft mc = Minecraft.getInstance();
			MultiBufferSource.BufferSource bufferSrc = mc.renderBuffers().bufferSource();
			mc.getBlockRenderer().renderSingleBlock(dsu.getBlockState(), pose, bufferSrc, light, overlay);
			bufferSrc.endBatch();
		}
		ItemStack stored = dsu.inventory.getStackInSlot(0);
		if (!stored.isEmpty()) {
			pose.pushPose();
			pose.scale(0.5f, 0.5f, 0.5f);
			pose.translate(1, 1.45, 1);
			for (Direction dir : Direction.values()) {
				if (dir.getAxis().isVertical()) continue;
				if (dsu.hasLevel()) {
					BlockPos neighborPos = dsu.getBlockPos().relative(dir);
					if (!Block.shouldRenderFace(dsu.getBlockState(), dsu.getLevel(), dsu.getBlockPos(), dir, neighborPos)) {
						continue;
					}
				}
				pose.pushPose();
				pose.mulPose(Axis.YP.rotationDegrees(180 - (90.0F * dir.get2DDataValue())));
				pose.translate(0, 0,-1.005);
				Minecraft.getInstance().getItemRenderer().render(
					stored, ItemDisplayContext.FIXED, false,
					pose, buff, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
					Minecraft.getInstance().getItemRenderer().getModel(stored, dsu.getLevel(), null, 0)
				);
				pose.translate(-(float)2/16, -1.45 + (float)9/16, 0);
				RendererHelper.renderTextInWorld(StringUtils.formatNumberAuto(stored.getCount(), 4), pose, buff);
				pose.popPose();
			}
			pose.mulPose(Axis.XP.rotationDegrees(90));
			pose.popPose();
		}
	}
}
