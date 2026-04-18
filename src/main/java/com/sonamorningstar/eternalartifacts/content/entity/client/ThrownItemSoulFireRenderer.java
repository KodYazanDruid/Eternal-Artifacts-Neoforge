package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.client.render.EntityRendererHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;

public class ThrownItemSoulFireRenderer<T extends Entity & ItemSupplier> extends ThrownItemRenderer<T> {
	public ThrownItemSoulFireRenderer(EntityRendererProvider.Context context, float scale, boolean fullBright) {
		super(context, scale, fullBright);
	}
	
	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		if (entity.isOnFire() && !entity.isSpectator()) {
			EntityRendererHelper.renderSoulFlame(poseStack, buffer, entity);
		}
	}
}
