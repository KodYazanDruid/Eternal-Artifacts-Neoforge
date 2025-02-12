package com.sonamorningstar.eternalartifacts.mixin_helper.ducking;

import net.minecraft.world.entity.LivingEntity;

public interface ILivingDasher {
	String KEY = "DashTokens";

	void dashAir(LivingEntity living);
	
	int dashCooldown();
	
	void setDashCooldown(int ticks);
}
