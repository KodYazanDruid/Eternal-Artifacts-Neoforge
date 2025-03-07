package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.EntityExposer;
import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.EntityJumpFactor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityJumpFactor, EntityExposer {
	@Shadow protected abstract float getBlockJumpFactor();
	@Shadow protected abstract void setSharedFlag(int flag, boolean set);
	
	@Shadow protected boolean wasTouchingWater;
	
	@Override
	public float getJumpFactor() {
		return getBlockJumpFactor();
	}
	
	@Override
	public void setWasTouchingWater(boolean wasTouchingWater) {
		this.wasTouchingWater = wasTouchingWater;
	}
	
	@Override
	public void setSharedFlagExp(int flag, boolean set) {
		this.setSharedFlag(flag, set);
	}
}
