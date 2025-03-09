package com.sonamorningstar.eternalartifacts.mixin_helper.ducking;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface EntityExposer {
	
	void setWasTouchingWater(boolean wasTouchingWater);
	
	void setSharedFlagExp(int flag, boolean set);
	
	void forceVehicle(@Nullable Entity vehicle);
}
