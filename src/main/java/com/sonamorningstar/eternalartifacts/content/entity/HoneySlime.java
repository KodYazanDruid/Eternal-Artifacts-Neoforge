package com.sonamorningstar.eternalartifacts.content.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

public class HoneySlime extends Slime {
	public HoneySlime(EntityType<? extends Slime> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}
	
	@Override
	protected ParticleOptions getParticleType() {
		return ParticleTypes.FALLING_HONEY;
	}
}
