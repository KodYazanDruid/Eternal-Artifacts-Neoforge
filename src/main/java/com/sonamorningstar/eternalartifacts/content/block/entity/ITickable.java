package com.sonamorningstar.eternalartifacts.content.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickable {

    void tick(Level lvl, BlockPos pos, BlockState st);


}
