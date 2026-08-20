package com.sonamorningstar.eternalartifacts.mixin_helper.ducking;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockItemCanPlace {
	boolean canPlaceBI(BlockPlaceContext context, BlockState state);
}
