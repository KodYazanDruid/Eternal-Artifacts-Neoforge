package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

public class IceBrickSlab extends SlabBlock {
    public IceBrickSlab(Properties props) {
        super(props);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction dir) {
        SlabType type = state.getValue(TYPE);
        boolean flag = adjacent.is(state.getBlock());
        if (flag){
            SlabType adjacentType = adjacent.getValue(TYPE);
            flag = type.equals(adjacentType);
        }
        flag = flag || adjacent.is(ModBlocks.ICE_BRICKS);
        if (adjacent.is(ModBlocks.ICE_BRICK_STAIRS) && dir.get2DDataValue() != -1) {
            Half half = adjacent.getValue(IceBrickStairs.HALF);
            flag = flag || half.toString().equals(type.toString());
        }
        return flag || super.skipRendering(state, adjacent, dir);
    }
}
