package com.sonamorningstar.eternalartifacts.content.entity.projectile.flyingitem;

import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface ProjectileMovement {
	Vec3 updateVelocity(FlyingItemProjectile projectile, Vec3 currentVelocity, int ageInTicks);
}