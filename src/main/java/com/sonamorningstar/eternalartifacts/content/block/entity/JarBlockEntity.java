package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class JarBlockEntity extends ModBlockEntity {
    public JarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.JAR.get(), pPos, pBlockState);
    }

    public ModFluidStorage tank = new ModFluidStorage(1000) {
        @Override
        protected void onContentsChanged() {
            JarBlockEntity.this.requestModelDataUpdate();
            JarBlockEntity.this.sendUpdate();
        }
    };

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        tank.readFromNBT(pTag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        tank.writeToNBT(pTag);
    }


}
