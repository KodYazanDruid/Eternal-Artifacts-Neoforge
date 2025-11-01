package com.sonamorningstar.eternalartifacts.content.entity.projectile;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PrismarineArrow extends AbstractArrow {
	public PrismarineArrow(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
		super(pEntityType, pLevel, ModItems.PRISMARINE_ARROW.toStack());
		setBaseDamage(4.0);
	}
	
	public PrismarineArrow(Level pLevel, LivingEntity pOwner, ItemStack pPickupItemStack) {
		super(ModEntities.PRISMARINE_ARROW.get(), pOwner, pLevel, pPickupItemStack);
		setBaseDamage(4.0);
	}
	
	public PrismarineArrow(Level pLevel, double pX, double pY, double pZ, ItemStack pPickupItemStack) {
		super(ModEntities.PRISMARINE_ARROW.get(), pX, pY, pZ, pLevel, pPickupItemStack);
		setBaseDamage(4.0);
	}
	
	@Override
	protected float getWaterInertia() {
		return 1.0F;
	}
}
