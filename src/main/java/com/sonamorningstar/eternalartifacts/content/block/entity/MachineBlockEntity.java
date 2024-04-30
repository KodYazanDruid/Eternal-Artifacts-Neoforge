package com.sonamorningstar.eternalartifacts.content.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class MachineBlockEntity extends ModBlockEntity {
    private final BlockPos pos;
    private IItemHandler inventory;
    private IFluidHandler tank;
    private IEnergyStorage energy;
    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.pos = pos;
        if(level != null){
            this.inventory = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            this.tank = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
            this.energy = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
        }
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> throw new IllegalStateException("Unexpected value: " + index);
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 ->  progress = value;
                    case 1 -> maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    protected ContainerData data;
    protected int progress;
    protected int maxProgress = 100;

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
    }

    public void drops() {
        if(inventory != null){
            SimpleContainer container = new SimpleContainer(inventory.getSlots());
            for (int i = 0; i < inventory.getSlots(); i++) {
                container.setItem(i, inventory.getStackInSlot(i));
            }
            Containers.dropContents(this.level, this.worldPosition, container);
        }
    }
}
