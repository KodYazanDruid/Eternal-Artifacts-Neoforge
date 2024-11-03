package com.sonamorningstar.eternalartifacts.content.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class IceBricksBlock extends Block {
    public IceBricksBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction dir) {
        return adjacent.is(state.getBlock()) || super.skipRendering(state, adjacent, dir);
    }
}
