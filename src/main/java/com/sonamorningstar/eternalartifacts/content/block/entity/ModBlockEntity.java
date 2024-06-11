package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ModBlockEntity extends BlockEntity {
    public ModBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    protected boolean shouldSyncOnUpdate() {
        return false;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return shouldSyncOnUpdate() ? ClientboundBlockEntityDataPacket.create(this) : null;
    }

    protected void saveSynced(CompoundTag tag) {}

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveSynced(tag);
        saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        saveSynced(pTag);
    }

    public void sendUpdate(){
        setChanged();
        if(level != null && !isRemoved() && level.hasChunkAt(worldPosition)) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    protected ModFluidStorage createBasicTank(int cap) {
        return new ModFluidStorage(cap) {
            @Override
            protected void onContentsChanged() {
                sendUpdate();
            }
        };
    }

    protected ModFluidStorage createBasicTank(int cap, Predicate<FluidStack> validator) {
        return new ModFluidStorage(cap, validator) {
            @Override
            protected void onContentsChanged() {
                sendUpdate();
            }
        };
    }
}
