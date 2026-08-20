package com.sonamorningstar.eternalartifacts.content.entity.projectile.flyingitem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;

public final class ProjectileMovements {
	
	private ProjectileMovements() {}
	
	public static ProjectileMovement homing(LivingEntity target, double speed, double turnStrength) {
		return (projectile, currentVelocity, age) -> {
			if (target == null || !target.isAlive()) {
				return currentVelocity;
			}
			Vec3 toTarget = target.getBoundingBox().getCenter()
				.subtract(projectile.position())
				.normalize()
				.scale(speed);
			return currentVelocity.scale(1.0 - turnStrength).add(toTarget.scale(turnStrength));
		};
	}
	
	public static ProjectileMovement custom(BiFunction<FlyingItemProjectile, Integer, Vec3> velocityFunction) {
		return (projectile, currentVelocity, age) -> velocityFunction.apply(projectile, age);
	}
}