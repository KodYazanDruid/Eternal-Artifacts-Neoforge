package com.sonamorningstar.eternalartifacts.content.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PinkSlimeBlock extends SlimeBlock {
    public PinkSlimeBlock(Properties p_56402_) {
        super(p_56402_);
    }

    @Override
    public boolean isStickyBlock(BlockState state) {
        return true;
    }

    @Override
    public boolean canStickTo(BlockState state, BlockState other) {
        return !other.isStickyBlock() || other.is(this);
    }
}
