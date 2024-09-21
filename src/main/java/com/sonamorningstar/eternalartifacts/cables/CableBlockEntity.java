package com.sonamorningstar.eternalartifacts.cables;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CableBlockEntity extends ModBlockEntity {
    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE.get(), pos, state);
    }

    public void tickServer(Level lvl, BlockPos pos, BlockState st) {

    }
}
