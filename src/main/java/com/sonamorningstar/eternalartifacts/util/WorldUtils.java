package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class WorldUtils {
	public static <E extends Entity> List<E> getEntitiesInRange(Level level, Vec3 center, double radius,
																Class<E> entityClass, Predicate<E> entityFilter) {
		return level
			.getEntitiesOfClass(entityClass, AABB.ofSize(center, radius * 2, radius * 2, radius * 2), entityFilter)
			.stream()
			.filter(e -> {
				AABB aabb = e.getBoundingBox();
				double x = Math.max(aabb.minX, Math.min(center.x, aabb.maxX));
				double y = Math.max(aabb.minY, Math.min(center.y, aabb.maxY));
				double z = Math.max(aabb.minZ, Math.min(center.z, aabb.maxZ));
				double dx = x - center.x;
				double dy = y - center.y;
				double dz = z - center.z;
				return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
			})
			.toList();
	}
	
	public static <E extends Entity> List<E> getEntitiesInCone(Level level, Vec3 origin, Vec3 direction, double range, double angleDegrees,
												Class<E> entityClass, Predicate<E> entityFilter) {
		double cosAngle = Math.cos(Math.toRadians(angleDegrees));
		Vec3 dirNorm = direction.normalize();
		return level
			.getEntitiesOfClass(entityClass, AABB.ofSize(origin.add(dirNorm.scale(range / 2)), range * 2, range * 2, range * 2), entityFilter)
			.stream()
			.filter(e -> {
				AABB aabb = e.getBoundingBox();
				double x = Math.max(aabb.minX, Math.min(origin.x, aabb.maxX));
				double y = Math.max(aabb.minY, Math.min(origin.y, aabb.maxY));
				double z = Math.max(aabb.minZ, Math.min(origin.z, aabb.maxZ));
				double dx = x - origin.x;
				double dy = y - origin.y;
				double dz = z - origin.z;
				double distSqr = dx * dx + dy * dy + dz * dz;

				if (distSqr > range * range) return false;

				Vec3 toEntity = aabb.getCenter().subtract(origin).normalize();
				return toEntity.dot(dirNorm) >= cosAngle;
			})
			.toList();
	}
}
