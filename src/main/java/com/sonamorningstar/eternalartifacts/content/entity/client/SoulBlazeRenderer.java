package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Blaze;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class SoulBlazeRenderer extends BlazeRenderer {
	public SoulBlazeRenderer(EntityRendererProvider.Context p_173933_) {
		super(p_173933_);
	}
	
	@Override
	public void render(Blaze p_entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(p_entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Blaze pEntity) {
		return new ResourceLocation(MODID,"textures/entity/soul_blaze.png");
	}
}
