package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

public class ModItemMultiFluidTank<T extends ModFluidStorage> implements IFluidHandlerItem {
    private final ItemStack stack;
    private final List<T> tanks;

    public ModItemMultiFluidTank(ItemStack stack, List<T> tanks) {
        this.stack = stack;
        this.tanks = tanks;
        tanks.forEach(tank -> tank.addListener(this::onContentsChange));
        CompoundTag tag = stack.getTag();
        if (tag != null) deserializeNBT(tag);
    }

    public void deserializeNBT(CompoundTag nbt) {
        ListTag tanksList = nbt.getList("Tanks", 10);
        for(int i = 0; i < tanksList.size() ; i++) {
            CompoundTag entry = tanksList.getCompound(i);
            FluidStack stack = FluidStack.loadFluidStackFromNBT(entry);
            int tankNo = entry.getInt("TankNo");
            tanks.get(tankNo).setFluid(stack, 0);
        }
    }
    public CompoundTag serializeNBT(CompoundTag nbt) {
        ListTag tanksList = new ListTag();
        for(int i = 0; i < tanks.size(); i++) {
            T tank = tanks.get(i);
            CompoundTag entry = new CompoundTag();
            FluidStack stack = tank.getFluidInTank(0);
            if (stack.isEmpty()) continue;
            stack.writeToNBT(entry);
            entry.putInt("TankNo", i);
            tanksList.add(entry);
        }
        nbt.put("Tanks", tanksList);
        return nbt;
    }

    //region Regular stuff
    @Override
    public ItemStack getContainer() {return stack;}
    @Override
    public int getTanks() {return tanks.size();}
    @Override
    public FluidStack getFluidInTank(int tank) {return tanks.get(tank).getFluidInTank(tank);}
    @Override
    public int getTankCapacity(int tank) {return tanks.get(tank).getTankCapacity(tank);}
    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {return tanks.get(tank).isFluidValid(tank, stack);}
    //endregion

    //region Transfers
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled;
        for(T tank : tanks) {
            filled = tank.fill(resource, FluidAction.SIMULATE);
            if(filled > 0) {
                filled = tank.fill(resource, action);
                return filled;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained;
        for(T tank : tanks) {
            drained = tank.drain(resource, FluidAction.SIMULATE);
            if(!drained.isEmpty()) {
                drained = tank.drain(resource, action);
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained;
        for(T tank : tanks) {
            drained = tank.drain(maxDrain, FluidAction.SIMULATE);
            if(!drained.isEmpty()) {
                drained = tank.drain(maxDrain, action);
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }
    //endregion

    public void setTank(int tankNo, FluidStack fluidStack) {
        tanks.get(tankNo).setFluid(fluidStack, 0);
        onContentsChange();
    }

    protected void onContentsChange() {
        CompoundTag tag = stack.getOrCreateTag();
        serializeNBT(tag);
    }

    public T getTank(int tankNo) {
        return tanks.get(tankNo);
    }
}
