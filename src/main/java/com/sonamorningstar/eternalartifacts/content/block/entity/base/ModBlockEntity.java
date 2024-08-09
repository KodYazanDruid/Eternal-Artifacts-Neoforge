package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ModBlockEntity extends BlockEntity {
    public ModBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

    protected ModFluidStorage createBasicTank(int size) {
        return new ModFluidStorage(size) {
            @Override
            protected void onContentsChanged() {
                sendUpdate();
            }
        };
    }

    protected ModFluidStorage createBasicTank(int size, Predicate<FluidStack> validator, boolean canDrain, boolean canFill) {
        return new ModFluidStorage(size, validator) {
            @Override
            protected void onContentsChanged() {
                sendUpdate();
            }

            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                return canDrain ? super.drain(maxDrain, action) : FluidStack.EMPTY;
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                return canFill ? super.fill(resource, action) : 0;
            }
        };
    }

    protected ModEnergyStorage createBasicEnergy(int size, int transfer) {
        return createBasicEnergy(size, transfer, transfer);
    }
    protected ModEnergyStorage createBasicEnergy(int size, int maxReceive, int maxExtract) {
        return new ModEnergyStorage(size, maxReceive, maxExtract) {
            @Override
            public void onEnergyChanged() {
                sendUpdate();
            }

            @Override
            public boolean canExtract() {
                return false;
            }
        };
    }
    protected ModEnergyStorage createDefaultEnergy() {
        return createBasicEnergy(50000, 2500);
    }

    protected ModItemStorage createBasicInventory(int size) {
        return createBasicInventory(size, true);
    }
    protected ModItemStorage createBasicInventory(int size, boolean canInsert) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return canInsert;
            }
        };
    }
    protected ModItemStorage createBasicInventory(int size, BiPredicate<Integer, ItemStack> isValid) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return isValid.test(slot, stack);
            }
        };
    }
}
