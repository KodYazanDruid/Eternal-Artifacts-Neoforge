package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.util.SpellDamageHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SpellWitherSkull extends WitherSkull {
	private float damage;
	
	public SpellWitherSkull(EntityType<? extends WitherSkull> entityType, Level level) {
		super(entityType, level);
	}
	
	public SpellWitherSkull(Level level, LivingEntity shooter, double offsetX, double offsetY, double offsetZ, float damage) {
		super(level, shooter, offsetX, offsetY, offsetZ);
		this.damage = damage;
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		if (!this.level().isClientSide) {
			Entity victim = result.getEntity();
			Entity owner = this.getOwner();
			boolean hurt;
			if (owner instanceof LivingEntity livingentity) {
				hurt = victim.hurt(this.damageSources().witherSkull(this, livingentity), damage);
				if (hurt) {
					if (victim.isAlive()) {
						this.doEnchantDamageEffects(livingentity, victim);
					} else {
						livingentity.heal(damage);
					}
				}
			} else {
				hurt = SpellDamageHelper.hurtWithSpellDamage(this, victim, damage);
			}
			
			if (hurt && victim instanceof LivingEntity livingentity1) {
				livingentity1.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1), this.getEffectSource());
			}
		}
	}
	
	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putFloat("SpellDamage", damage);
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		damage = tag.getFloat("SpellDamage");
	}
}
