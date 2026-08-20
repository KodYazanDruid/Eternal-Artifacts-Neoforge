package com.sonamorningstar.eternalartifacts.content.entity.client.flyingitem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.flyingitem.FlyingItemProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FlyingItemProjectileRenderer extends EntityRenderer<FlyingItemProjectile> {
	
	public FlyingItemProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.15F;
	}
	
	@Override
	public void render(FlyingItemProjectile entity, float entityYaw, float partialTicks,
					   PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		
		ItemStack stack = entity.getItem();
		if (stack.isEmpty()) {
			super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
			return;
		}
		
		poseStack.pushPose();
		
		ProjectileOrientation orientation = switch (entity.getOrientationMode()) {
			case POINT_FORWARD -> ProjectileOrientations.pointForward();
			case TUMBLING -> ProjectileOrientations.tumbling();
			case SIMPLE_SPIN -> ProjectileOrientations.simpleSpin();
		};
		orientation.apply(poseStack, entity, partialTicks);
		
		Minecraft.getInstance().getItemRenderer().renderStatic(
			stack,
			ItemDisplayContext.FIXED,
			packedLight,
			OverlayTexture.NO_OVERLAY,
			poseStack,
			bufferSource,
			entity.level(),
			entity.getId()
		);
		
		poseStack.popPose();
		super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
	}
	
	@Override
	public ResourceLocation getTextureLocation(FlyingItemProjectile entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}