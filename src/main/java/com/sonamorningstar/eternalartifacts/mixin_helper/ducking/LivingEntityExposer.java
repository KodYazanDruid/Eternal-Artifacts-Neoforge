package com.sonamorningstar.eternalartifacts.mixin_helper.ducking;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface LivingEntityExposer {
	
	private LivingEntity self() {
		return (LivingEntity) this;
	}
	
	void setFallFlyTicks(int ticks);
	void setLivingEntityFlagExp(int key, boolean value);
	
	float getSwimAmountExp();
	float getSwimAmount0Exp();
	void setSwimAmountExp(float amount);
	void setSwimAmount0Exp(float amount);
	
	int getAutoSpinAttackTicks();
	void setAutoSpinAttackTicks(int ticks);
	
	void setUseItemExp(ItemStack stack);
	void setUseItemRemainingTicksExp(int ticks);
	void updateUsingItemExp(ItemStack stack);
}
