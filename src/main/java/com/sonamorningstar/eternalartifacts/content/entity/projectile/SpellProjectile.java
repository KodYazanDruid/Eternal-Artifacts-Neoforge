package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class SpellProjectile extends Projectile {
	public SpellProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}
	
	@Override
	protected void defineSynchedData() {
	
	}
}
