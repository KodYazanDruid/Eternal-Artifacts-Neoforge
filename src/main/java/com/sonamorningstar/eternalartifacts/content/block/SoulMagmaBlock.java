package com.sonamorningstar.eternalartifacts.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SoulMagmaBlock extends MagmaBlock {
	public SoulMagmaBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!entity.isSteppingCarefully() && entity instanceof LivingEntity living && !EnchantmentHelper.hasFrostWalker(living)) {
			entity.hurt(level.damageSources().hotFloor(), 2.5F);
		}
	}
}
