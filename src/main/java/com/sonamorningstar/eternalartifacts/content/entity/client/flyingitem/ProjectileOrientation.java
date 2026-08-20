package com.sonamorningstar.eternalartifacts.content.entity.client.flyingitem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.content.entity.projectile.flyingitem.FlyingItemProjectile;

@FunctionalInterface
public interface ProjectileOrientation {
	void apply(PoseStack poseStack, FlyingItemProjectile entity, float partialTicks);
}
