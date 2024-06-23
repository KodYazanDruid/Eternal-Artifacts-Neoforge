package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickableClient {

    void tickClient(Level lvl, BlockPos pos, BlockState st);

}
