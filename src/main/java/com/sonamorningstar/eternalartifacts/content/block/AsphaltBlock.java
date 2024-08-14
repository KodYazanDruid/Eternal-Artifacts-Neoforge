package com.sonamorningstar.eternalartifacts.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AsphaltBlock extends Block {
    public AsphaltBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity living)
            living.setDeltaMovement(living.getDeltaMovement().x * 1.2F, living.getDeltaMovement().y, living.getDeltaMovement().z * 1.2F);
        super.stepOn(level, pos, state, entity);
    }
}
