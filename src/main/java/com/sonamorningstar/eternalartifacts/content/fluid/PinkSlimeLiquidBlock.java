package com.sonamorningstar.eternalartifacts.content.fluid;

import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class PinkSlimeLiquidBlock extends LiquidBlock {
    public PinkSlimeLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties props) {
        super(fluid, props.randomTicks());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getFluidState().isSourceOfType(ModFluids.PINK_SLIME.get());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        ModEntities.PINKY.get().spawn(level, pos, MobSpawnType.EVENT);
    }
}
