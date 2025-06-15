package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.entity.projectile.AmethystArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AmethystArrowItem extends ArrowItem {
	public AmethystArrowItem(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
		return new AmethystArrow(pLevel, pShooter, pStack.copyWithCount(1));
	}
}
