package com.sonamorningstar.eternalartifacts.content.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;

public class SoulBlaze extends Blaze {
	public SoulBlaze(EntityType<? extends Blaze> entityType, Level level) {
		super(entityType, level);
	}
	
	//Returning false because i want to render blue flames on the renderer.
	@Override
	public boolean isOnFire() {
		return false;
	}
}
