package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.DrumBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class DrumBlockEntity extends ModBlockEntity {
    public ModFluidStorage tank;
    public DrumBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRUM.get(), pos, state);
        this.tank = createBasicTank(((DrumBlock) state.getBlock()).getCapacity());
    }

    @Override
    protected boolean shouldSyncOnUpdate() { return true; }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.deserializeNBT(tag.getCompound("Fluid"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Fluid", tank.serializeNBT());
    }
}
