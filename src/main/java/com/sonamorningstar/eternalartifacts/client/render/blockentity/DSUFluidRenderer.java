package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.client.render.util.RendererHelper;
import com.sonamorningstar.eternalartifacts.content.block.entity.DeepFluidStorageUnit;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;

public class DSUFluidRenderer implements BlockEntityRenderer<DeepFluidStorageUnit> {
	@Override
	public void render(DeepFluidStorageUnit dsu, float delta, PoseStack pose, MultiBufferSource buff, int light, int overlay) {
		//RenderSystem.enableBlend();
		//RenderSystem.defaultBlendFunc();
		//if (!dsu.hasLevel()) Minecraft.getInstance().getBlockRenderer().renderSingleBlock(dsu.getBlockState(), pose, buff, light, overlay);
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
				RenderSystem.runAsFancy(() -> {
					RendererHelper.renderFluidTile(stored, pose, buff, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
				});
				pose.translate(0.5 - (float)2/16, -0.85 + (float)9/16, 0);
				RendererHelper.renderTextInWorld(stored.getAmount() +"ieYğ", pose, buff);
				pose.popPose();
			}
			pose.mulPose(Axis.XP.rotationDegrees(90));
			pose.popPose();
		}
		//RenderSystem.disableBlend();
	}
}
