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

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModBlockEntity extends BlockEntity {
    public ModBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected boolean shouldSyncOnUpdate() {
        return false;
    }

    protected void findRecipe() {};

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

    protected ModFluidStorage createDefaultTank() {return createBasicTank(16000);}
    protected ModFluidStorage createBasicTank(int size, Runnable... run) {
        return new ModFluidStorage(size) {
            @Override
            protected void onContentsChanged() {
                sendUpdate();
                for (Runnable runnable : run) runnable.run();
            }
        };
    }
    protected ModFluidStorage createRecipeFinderTank(int size) {
        return new ModFluidStorage(size) {
            @Override
            protected void onContentsChanged() {
                findRecipe();
                sendUpdate();
            }
        };
    }
    protected ModFluidStorage createBasicTank(int size, boolean canDrain, boolean canFill, Runnable... run) {
        return new ModFluidStorage(size) {
            @Override
            protected void onContentsChanged() {
                sendUpdate();
                for (Runnable runnable : run) runnable.run();
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
    protected ModFluidStorage createRecipeFinderTank(int size, boolean canDrain, boolean canFill) {
        return new ModFluidStorage(size) {
            @Override
            protected void onContentsChanged() {
                findRecipe();
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
    protected ModFluidStorage createBasicTank(int size, Predicate<FluidStack> validator, boolean canDrain, boolean canFill, Runnable... run) {
        return new ModFluidStorage(size, validator) {
            @Override
            protected void onContentsChanged() {
                sendUpdate();
                for (Runnable runnable : run) runnable.run();
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
    protected ModFluidStorage createRecipeFinderTank(int size, Predicate<FluidStack> validator, boolean canDrain, boolean canFill) {
        return new ModFluidStorage(size, validator) {
            @Override
            protected void onContentsChanged() {
                findRecipe();
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

    protected ModEnergyStorage createDefaultEnergy() {return createBasicEnergy(50000, 2500, true, false);}
    protected ModEnergyStorage createBasicEnergy(int size, int transfer, boolean canReceive, boolean canExtract) {
        return createBasicEnergy(size, transfer, transfer, canReceive, canExtract);
    }
    protected ModEnergyStorage createBasicEnergy(int size, int maxReceive, int maxExtract, boolean canReceive, boolean canExtract) {
        return new ModEnergyStorage(size, maxReceive, maxExtract) {
            @Override
            public void onEnergyChanged() {
                sendUpdate();
            }
            @Override
            public boolean canReceive() {return canReceive;}
            @Override
            public boolean canExtract() {return canExtract;}
        };
    }

    @SafeVarargs
    protected final ModItemStorage createBasicInventory(int size, boolean canInsert, Consumer<Integer>... consumers) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                sendUpdate();
                for (Consumer<Integer> consumer : consumers) consumer.accept(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return canInsert;
            }
        };
    }
    @SafeVarargs
    protected final ModItemStorage createBasicInventory(int size, List<Integer> outputSlots, Consumer<Integer>... consumers) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                sendUpdate();
                for (Consumer<Integer> consumer : consumers) consumer.accept(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {return !outputSlots.contains(slot);}
        };
    }
    protected final ModItemStorage createRecipeFinderInventory(int size, List<Integer> outputSlots) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                if (!outputSlots.contains(slot)) findRecipe();
                sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {return !outputSlots.contains(slot);}
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
    protected ModItemStorage createRecipeFinderInventory(int size, BiPredicate<Integer, ItemStack> isValid) {
        return new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                findRecipe();
                sendUpdate();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return isValid.test(slot, stack);
            }
        };
    }
}
