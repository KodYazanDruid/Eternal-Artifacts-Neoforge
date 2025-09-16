package com.sonamorningstar.eternalartifacts.content.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Slime;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class HoneySlimeRenderer extends SlimeRenderer {
	
	public HoneySlimeRenderer(EntityRendererProvider.Context p_174391_) {
		super(p_174391_);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Slime pEntity) {
		return new ResourceLocation(MODID,"textures/entity/honey_slime.png");
	}
}
