package com.sonamorningstar.eternalartifacts.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.client.resources.model.TwoLayerSkullModel;
import com.sonamorningstar.eternalartifacts.content.block.ModSkullBlock;
import com.sonamorningstar.eternalartifacts.core.ModSkullType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class ModSkullBlockRenderer extends SkullBlockRenderer {
	
	public ModSkullBlockRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}
	
	@Override
	public void render(SkullBlockEntity skullBe, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		float mouthAnim = skullBe.getAnimation(pPartialTick);
		BlockState blockstate = skullBe.getBlockState();
		boolean isWall = blockstate.getBlock() instanceof WallSkullBlock;
		Direction direction = isWall ? blockstate.getValue(WallSkullBlock.FACING) : null;
		int i = isWall ? RotationSegment.convertToSegment(direction.getOpposite()) : blockstate.getValue(SkullBlock.ROTATION);
		float yRot = RotationSegment.convertToDegrees(i);
		SkullBlock.Type skullblock$type = ((AbstractSkullBlock)blockstate.getBlock()).getType();
		if (skullblock$type instanceof ModSkullType mst) {
			SkullModelBase model = modelByType.get(mst);
			if (model instanceof TwoLayerSkullModel tlsm) {
				renderModSkull(direction, yRot, mouthAnim,
					pPoseStack, pBuffer, pPackedLight, tlsm);
			} else {
				super.render(skullBe, pPartialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
			}
		} else {
			super.render(skullBe, pPartialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
		}
	}
	
	public static void renderModSkull(Direction direction, float yRot, float mouthAnim,
									   PoseStack pose, MultiBufferSource buffer, int light, TwoLayerSkullModel tlsm) {
		Pair<ResourceLocation, ResourceLocation> pair = ModSkullBlock.LAYERED_SKIN_BY_TYPE.get(tlsm.getSkullType());
		if (pair == null) {
			return;
		}
			
		pose.pushPose();
		if (direction == null) {
			pose.translate(0.5F, 0.0F, 0.5F);
		} else {
			pose.translate(0.5F - (float)direction.getStepX() * 0.25F, 0.25F, 0.5F - (float)direction.getStepZ() * 0.25F);
		}
		
		pose.scale(-1.0F, -1.0F, 1.0F);
		
		tlsm.setupAnim(mouthAnim, yRot, 0.0F);
		
		VertexConsumer baseConsumer = buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(pair.getFirst()));
		tlsm.getHead().render(pose, baseConsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		Minecraft.getInstance().renderBuffers().bufferSource().endLastBatch();
		VertexConsumer overlayConsumer = buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(pair.getSecond()));
		tlsm.getOverlay().render(pose, overlayConsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		Minecraft.getInstance().renderBuffers().bufferSource().endLastBatch();
		
		pose.popPose();
	}

}
