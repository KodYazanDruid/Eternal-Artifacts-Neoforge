package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface TickableClient {

    void tickClient(ClientLevel lvl, BlockPos pos, BlockState st);

}
