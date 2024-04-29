package com.sonamorningstar.eternalartifacts.content.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MachineBlockEntity extends ModBlockEntity {
    public MachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }
}
