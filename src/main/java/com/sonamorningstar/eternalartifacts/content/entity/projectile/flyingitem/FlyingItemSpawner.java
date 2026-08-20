package com.sonamorningstar.eternalartifacts.content.entity.projectile.flyingitem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class FlyingItemSpawner {
	
	private FlyingItemSpawner() {}
	
	public static <T extends FlyingItemProjectile> T shootFromLook(
		EntityType<T> type, Level level, LivingEntity shooter,
		ItemStack renderItem, float speed, float damage) {
		
		T projectile = type.create(level);
		if (projectile == null) return null;
		
		projectile.setOwner(shooter);
		projectile.setItem(renderItem);
		projectile.setDamage(damage);
		projectile.setNoGravity(true);
		projectile.setDragFactor(1.0F);
		
		Vec3 look = shooter.getLookAngle().normalize();
		Vec3 spawnPos = shooter.position()
			.add(look.scale(0.6))
			.add(0, shooter.getEyeHeight() * 0.85, 0);
		
		projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
		//projectile.setPos(shooter.getX(), shooter.getEyeHeight(), shooter.getZ());
		projectile.setDeltaMovement(look.scale(speed));
		projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, speed, 1.0F);
		projectile.updateRotation();
		
		if (!level.isClientSide) {
			level.addFreshEntity(projectile);
			System.out.println("Summoned flying item with Pitch: "+projectile.getXRot()+", Yaw:"+projectile.getYRot());
		}
		return projectile;
	}
	
	public static <T extends FlyingItemProjectile> T dropFromSky(
		EntityType<T> type, Level level, LivingEntity owner, ItemStack renderItem,
		Vec3 targetPos, double startHeight, double initialSpeed, float damage) {
		
		T projectile = type.create(level);
		if (projectile == null) return null;
		
		projectile.setOwner(owner);
		projectile.setItem(renderItem);
		projectile.setDamage(damage);
		projectile.setNoGravity(false);
		projectile.setDragFactor(0.99F);
		
		projectile.setPos(targetPos.x, targetPos.y + startHeight, targetPos.z);
		projectile.setDeltaMovement(0, -initialSpeed, 0);
		projectile.updateRotation();
		
		if (!level.isClientSide) {
			level.addFreshEntity(projectile);
		}
		return projectile;
	}
	
	public static <T extends FlyingItemProjectile> T spawnWithMovement(
		EntityType<T> type, Level level, LivingEntity owner, ItemStack renderItem,
		Vec3 spawnPos, Vec3 initialVelocity, ProjectileMovement movement, float damage) {
		
		T projectile = type.create(level);
		if (projectile == null) return null;
		
		projectile.setOwner(owner);
		projectile.setItem(renderItem);
		projectile.setDamage(damage);
		projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
		projectile.setDeltaMovement(initialVelocity);
		projectile.setMovement(movement);
		projectile.updateRotation();
		
		if (!level.isClientSide) {
			level.addFreshEntity(projectile);
		}
		return projectile;
	}
}