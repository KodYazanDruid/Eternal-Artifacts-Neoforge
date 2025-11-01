package com.sonamorningstar.eternalartifacts.content.entity.client;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class PrismarineArrowRenderer extends ArrowRenderer {
	public PrismarineArrowRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Entity pEntity) {
		return new ResourceLocation(MODID, "textures/entity/projectiles/prismarine_arrow.png");
		
	}
}
