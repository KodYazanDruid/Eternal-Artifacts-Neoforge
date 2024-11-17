package com.sonamorningstar.eternalartifacts.content.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class IceBrickStairs extends StairBlock {
    public IceBrickStairs(Supplier<BlockState> p_56862_, Properties p_56863_) {
        super(p_56862_, p_56863_);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction dir) {
        return adjacent.is(state.getBlock()) || super.skipRendering(state, adjacent, dir);
    }
}
