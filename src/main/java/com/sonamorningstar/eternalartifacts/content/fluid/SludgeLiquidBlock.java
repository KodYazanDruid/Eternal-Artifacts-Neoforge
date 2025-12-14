package com.sonamorningstar.eternalartifacts.content.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class SludgeLiquidBlock extends LiquidBlock {
	public SludgeLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties p_54695_) {
		super(fluid, p_54695_);
	}
	
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity living) {
			MobEffectInstance poison = new MobEffectInstance(MobEffects.POISON, 300, 2);
			MobEffectInstance nausea = new MobEffectInstance(MobEffects.CONFUSION, 300, 2);
			living.addEffect(poison);
			living.addEffect(nausea);
		}
	}
}
