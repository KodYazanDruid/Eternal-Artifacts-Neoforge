package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.render.util.RendererHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.DeepFluidStorageUnit;
import com.sonamorningstar.eternalartifacts.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;

public class DSUFluidRenderer implements BlockEntityRenderer<DeepFluidStorageUnit> {
	@Override
	public void render(DeepFluidStorageUnit dsu, float delta, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
		if (!dsu.hasLevel()) {
			Minecraft mc = Minecraft.getInstance();
			MultiBufferSource.BufferSource bufferSrc = mc.renderBuffers().bufferSource();
			mc.getBlockRenderer().renderSingleBlock(dsu.getBlockState(), pose, bufferSrc, light, overlay);
			bufferSrc.endBatch();
		}
		FluidStack stored = dsu.tank.getFluidInTank(0);
		if (!stored.isEmpty()) {
			pose.pushPose();
			pose.scale(0.5f, 0.5f, 0.5f);
			pose.translate(1, 0.85, 1);
			for (Direction dir : Direction.values()) {
				if (dir.getAxis() == Direction.Axis.Y) continue;
				pose.pushPose();
				pose.mulPose(Axis.YP.rotationDegrees(90.0F * dir.get2DDataValue()));
				pose.translate(-0.5, 0,-1.005);
				RendererHelper.renderFluidTile(stored, pose, buff, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
				pose.translate(0.5 - (float)2/16, -0.85 + (float)9/16, 0);
				RendererHelper.renderTextInWorld(StringUtils.formatNumberAuto(stored.getAmount(), 4), pose, buff);
				pose.popPose();
			}
			pose.mulPose(Axis.XP.rotationDegrees(90));
			pose.popPose();
		}
	}
}
