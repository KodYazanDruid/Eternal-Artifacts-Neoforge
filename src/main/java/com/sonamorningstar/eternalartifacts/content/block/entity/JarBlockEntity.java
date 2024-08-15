package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class JarBlockEntity extends ModBlockEntity {
    public boolean isOpen = false;
    public JarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.JAR.get(), pPos, pBlockState);
    }

    @Getter
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
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.deserializeNBT(tag.getCompound("Fluid"));
        isOpen = tag.getBoolean(JarBlockItem.KEY_OPEN);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Fluid", tank.serializeNBT());
        tag.putBoolean(JarBlockItem.KEY_OPEN, isOpen);
    }


}
