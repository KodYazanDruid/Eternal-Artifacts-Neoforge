package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sonamorningstar.eternalartifacts.content.block.entity.PictureScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Matrix4f;

import static com.sonamorningstar.eternalartifacts.content.block.PictureScreenBlock.FACE_ROTATION;

public class PictureScreenRenderer implements BlockEntityRenderer<PictureScreen> {
	public PictureScreenRenderer(BlockEntityRendererProvider.Context context) {}
	
	@Override
	public void render(PictureScreen blockEntity, float partialTicks, PoseStack poseStack,
					   MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
		ResourceLocation texture = blockEntity.getImageResource();
		if (texture == null) {
			return;
		}
		
		Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.FACING);
		int faceRotation = blockEntity.getBlockState().getValue(FACE_ROTATION);
		float normalX = 0, normalY = 0, normalZ = 0;
		poseStack.pushPose();
		poseStack.translate(0.5, 0.5, 0.5);
		switch (facing) {
			case NORTH:
				normalZ = -1;
				break;
			case SOUTH:
				normalZ = 1;
				poseStack.mulPose(Axis.YP.rotationDegrees(180));
				break;
			case EAST:
				normalX = 1;
				poseStack.mulPose(Axis.YP.rotationDegrees(270));
				break;
			case WEST:
				normalX = -1;
				poseStack.mulPose(Axis.YP.rotationDegrees(90));
				break;
			case UP:
				normalY = 1;
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				break;
			case DOWN:
				normalY = -1;
				poseStack.mulPose(Axis.XP.rotationDegrees(270));
				break;
		}
		
		poseStack.mulPose(Axis.ZP.rotationDegrees(faceRotation * 90));
		poseStack.translate(0, 0, -0.501 + 12/16F);
		
		VertexConsumer builder = bufferSource.getBuffer(RenderType.entityCutout(texture));
		Matrix4f matrix = poseStack.last().pose();
		float size = 0.5f;

		builder.vertex(matrix, -size, size, 0)
			.color(255, 255, 255, 255)
			.uv(1, 0)
			.overlayCoords(OverlayTexture.NO_OVERLAY)
			.uv2(LightTexture.FULL_BRIGHT)
			.normal(normalX, normalY, normalZ)
			.endVertex();

		builder.vertex(matrix, size, size, 0)
			.color(255, 255, 255, 255)
			.uv(0, 0)
			.overlayCoords(OverlayTexture.NO_OVERLAY)
			.uv2(LightTexture.FULL_BRIGHT)
			.normal(normalX, normalY, normalZ)
			.endVertex();

		builder.vertex(matrix, size, -size, 0)
			.color(255, 255, 255, 255)
			.uv(0, 1)
			.overlayCoords(OverlayTexture.NO_OVERLAY)
			.uv2(LightTexture.FULL_BRIGHT)
			.normal(normalX, normalY, normalZ)
			.endVertex();

		builder.vertex(matrix, -size, -size, 0)
			.color(255, 255, 255, 255)
			.uv(1, 1)
			.overlayCoords(OverlayTexture.NO_OVERLAY)
			.uv2(LightTexture.FULL_BRIGHT)
			.normal(normalX, normalY, normalZ)
			.endVertex();
		
		poseStack.popPose();
	}
}
