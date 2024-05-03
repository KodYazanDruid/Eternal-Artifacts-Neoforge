package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capablities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capablities.ModItemStorage;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class MachineBlockEntity extends ModBlockEntity {
    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
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
    @Setter
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

    protected void fillTankFromSlot(ModItemStorage inventory, ModFluidStorage tank, int fluidSlot) {
        ItemStack stack = inventory.getStackInSlot(fluidSlot);
        if(!stack.isEmpty() && tank.getFluidAmount() < tank.getCapacity()) {
            IFluidHandlerItem itemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if(itemHandler != null && tank.isFluidValid(itemHandler.getFluidInTank(0)) && tank.getFluid().getFluid() == itemHandler.getFluidInTank(0).getFluid()) {
                int amountToDrain = tank.getCapacity() - tank.getFluidAmount();
                int amount = itemHandler.drain(amountToDrain, IFluidHandler.FluidAction.SIMULATE).getAmount();
                if (amount > 0) {
                    tank.fill(itemHandler.drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                }
                if (amount <= amountToDrain) {
                    inventory.setStackInSlot(fluidSlot, itemHandler.getContainer());
                }
            }
        }
    }

    public abstract void drops();
}
