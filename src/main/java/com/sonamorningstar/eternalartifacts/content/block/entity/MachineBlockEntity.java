package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capablities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.capablities.ModItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class MachineBlockEntity extends ModBlockEntity{
    private final ModEnergyStorage energyStorage;
    private final ModItemStorage itemStorage;

    public MachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int energyStorage, int energyTransfer, int slotSize) {
        super(pType, pPos, pBlockState);
        this.energyStorage = createEnergyStorage(energyStorage, energyTransfer, energyTransfer).get();
        this.itemStorage = createItemStorage(slotSize).get();
    }

    private Lazy<ModEnergyStorage> createEnergyStorage(int capacity, int max, int min) {
        return Lazy.of(() -> new ModEnergyStorage(capacity, max, min) {
            @Override
            public void onEnergyChanged() {
                sendUpdate();
            }
        });
    }

    private Lazy<ModItemStorage> createItemStorage(int size) {
        return Lazy.of(()-> new ModItemStorage(size) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                sendUpdate();
            }
        });
    }

    public IItemHandler getItemHandler() { return this.itemStorage; }


    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveSynced(CompoundTag tag) {
        super.saveSynced(tag);
    }

    public int getStoredEnergy() {
        return energyStorage.getEnergyStored();
    }
    public int getEnergyCapacity() {
        return energyStorage.getMaxEnergyStored();
    }
    public ItemStack getItemStack(int slot) {
        return itemStorage.getStackInSlot(slot);
    }

}
