package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.BouncingHoneySpellProj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;

public class BouncingHoneyRenderer extends EntityRenderer<BouncingHoneySpellProj> {
	public BouncingHoneyRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	
	@Override
	public void render(BouncingHoneySpellProj entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
		poseStack.pushPose();
		AABB boundingBox = entity.getBoundingBox();
		poseStack.scale((float) (boundingBox.getXsize()), (float) (boundingBox.getYsize()), (float) (boundingBox.getZsize()));
		poseStack.translate(-0.5, 0, -0.5);
		renderer.renderSingleBlock(Blocks.HONEY_BLOCK.defaultBlockState(), poseStack, buffer, packedLight,
			OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.translucent());
		poseStack.popPose();
	}
	
	@Override
	public ResourceLocation getTextureLocation(BouncingHoneySpellProj entity) {
		return null;
	}
}
