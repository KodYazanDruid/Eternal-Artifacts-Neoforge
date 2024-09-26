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

public class HotSpringWaterLiquidBlock extends LiquidBlock {
    public HotSpringWaterLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties props) {
        super(fluid, props);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living && !living.hasEffect(MobEffects.REGENERATION)) {
            MobEffectInstance effect = new MobEffectInstance(MobEffects.REGENERATION, 50, 1);
            living.addEffect(effect);
        }
    }
}
