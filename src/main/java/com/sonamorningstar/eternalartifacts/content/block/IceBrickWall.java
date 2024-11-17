package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

public class IceBrickWall extends WallBlock {
    public IceBrickWall(Properties p_57964_) {
        super(p_57964_);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction dir) {
        return adjacent.is(ModBlocks.ICE_BRICKS) || adjacent.is(state.getBlock()) || super.skipRendering(state, adjacent, dir);
    }
}
