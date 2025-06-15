package com.sonamorningstar.eternalartifacts.content.entity.client;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.AmethystArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AmethystArrowRenderer extends ArrowRenderer<AmethystArrow> {
	public AmethystArrowRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}
	
	@Override
	public ResourceLocation getTextureLocation(AmethystArrow pEntity) {
		return new ResourceLocation(MODID, "textures/entity/projectiles/amethyst_arrow.png");
	}
}
