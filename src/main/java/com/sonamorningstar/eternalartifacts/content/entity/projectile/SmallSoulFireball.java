package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SmallSoulFireball extends SmallFireball {
	public SmallSoulFireball(EntityType<? extends SmallFireball> entityType, Level level) {
		super(entityType, level);
	}
	
	public SmallSoulFireball(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ) {
		super(level, shooter, offsetX, offsetY, offsetZ);
	}
	
	public SmallSoulFireball(Level level, double x, double y, double z, double offsetX, double offsetY, double offsetZ) {
		super(level, x, y, z, offsetX, offsetY, offsetZ);
	}
	
	@Override
	public EntityType<?> getType() {
		return ModEntities.SOUL_SMALL_FIREBALL.get();
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		if (!this.level().isClientSide) {
			Entity entity = result.getEntity();
			Entity entity1 = this.getOwner();
			int i = entity.getRemainingFireTicks();
			entity.setSecondsOnFire(8);
			if (!entity.hurt(this.damageSources().fireball(this, entity1), 7.50F)) {
				entity.setRemainingFireTicks(i);
			} else if (entity1 instanceof LivingEntity) {
				this.doEnchantDamageEffects((LivingEntity)entity1, entity);
			}
		}
	}
	
	//Returning false because i want to render blue flames on the renderer.
	@Override
	public boolean displayFireAnimation() {
		return false;
	}
}
