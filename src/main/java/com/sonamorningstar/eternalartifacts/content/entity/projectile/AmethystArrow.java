package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AmethystArrow extends AbstractArrow {
	public AmethystArrow(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
		super(pEntityType, pLevel, ModItems.AMETHYST_ARROW.toStack());
		setBaseDamage(3.0);
	}
	
	public AmethystArrow(Level pLevel, LivingEntity pOwner, ItemStack pPickupItemStack) {
		super(ModEntities.AMETHYST_ARROW.get(), pOwner, pLevel, pPickupItemStack);
		setBaseDamage(3.0);
	}
	
	public AmethystArrow(Level pLevel, double pX, double pY, double pZ, ItemStack pPickupItemStack) {
		super(ModEntities.AMETHYST_ARROW.get(), pX, pY, pZ, pLevel, pPickupItemStack);
		setBaseDamage(3.0);
	}
	
	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide && !this.inGround) {
			this.level().addParticle(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
		}
	}
}
