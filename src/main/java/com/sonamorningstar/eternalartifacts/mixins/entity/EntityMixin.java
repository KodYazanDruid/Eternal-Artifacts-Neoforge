package com.sonamorningstar.eternalartifacts.mixins.entity;

import com.sonamorningstar.eternalartifacts.mixin_helper.ducking.EntityJumpFactor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityJumpFactor {
	@Shadow protected abstract float getBlockJumpFactor();
	
	@Override
	public float getJumpFactor() {
		return getBlockJumpFactor();
	}
}
