package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface TickableServer {

    void tickServer(ServerLevel lvl, BlockPos pos, BlockState st);

}
