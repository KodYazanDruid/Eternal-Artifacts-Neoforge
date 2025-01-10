package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.List;

public class MachineItemFluidStorage extends AbstractFluidTank implements IFluidHandlerItem {
    private final ItemStack stack;
    private final List<AbstractFluidTank> tanks = new ArrayList<>();

    public MachineItemFluidStorage(ItemStack stack) {
        this.stack = stack;
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            deserializeNBT(nbt);
        }
    }

    protected void onContentsChange() {
        stack.getOrCreateTag().put("Fluid", serializeNBT());
    }

    @Override
    public void setFluid(FluidStack stack, int tank) {
        get(tank).setFluid(stack, tank);
        onContentsChange();
    }

    @Override
    public FluidStack getFluid(int tank) {
        return get(tank).getFluidInTank(tank);
    }

    public void deserializeNBT(CompoundTag nbt) {
        CompoundTag fluidNBT = nbt.getCompound("Fluid");
        ListTag tanksList = fluidNBT.getList("Tanks", 10);
        int biggestTankIndex = fluidNBT.getInt("BiggestTankIndex");
        for(int i = 0; i < biggestTankIndex; i++) {
            AbstractFluidTank tank = new ModFluidStorage(0);
            tanks.add(tank);
        }
        for(int i = 0; i < tanksList.size() ; i++) {
            CompoundTag entry = tanksList.getCompound(i);
            FluidStack stack = FluidStack.loadFluidStackFromNBT(entry);
            int tankNo = entry.getInt("TankNo");
            AbstractFluidTank tank = new ModFluidStorage(stack.getAmount());
            tank.setFluid(stack, 0);
            tanks.add(tankNo, tank);
        }
    }
    public CompoundTag serializeNBT() {
        CompoundTag fluids = new CompoundTag();
        ListTag tanksList = new ListTag();
        for(int i = 0; i < tanks.size(); i++) {
            AbstractFluidTank tank = tanks.get(i);
            CompoundTag entry = new CompoundTag();
            FluidStack stack = tank.getFluidInTank(0);
            stack.writeToNBT(entry);
            entry.putInt("TankNo", i);
            tanksList.add(entry);
        }
        fluids.put("Tanks", tanksList);
        fluids.putInt("BiggestTankIndex", tanks.size());
        return fluids;
    }

    @Override
    public FluidStack drainForced(int i, FluidAction fluidAction) {
        return drain(i, fluidAction);
    }

    @Override
    public FluidStack drainForced(FluidStack stack, FluidAction fluidAction) {
        return drain(stack, fluidAction);
    }

    @Override
    public int fillForced(FluidStack stack, FluidAction fluidAction) {
        return fill(stack, fluidAction);
    }

    @Override
    public int getCapacity(int tank) {
        if (!isValidSlot(tank)) return 0;
        return get(tank).getTankCapacity(tank);
    }

    @Override
    public int getFluidAmount(int tank) {
        if (!isValidSlot(tank)) return 0;
        return get(tank).getFluidInTank(tank).getAmount();
    }

    @Override
    public AbstractFluidTank get(int i) {
        return tanks.get(i);
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (!isValidSlot(tank)) return FluidStack.EMPTY;
        return get(tank).getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return isValidSlot(tank) ? get(tank).getTankCapacity(tank) : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return isValidSlot(tank) && get(tank).isFluidValid(tank, stack);
    }

    private boolean isValidSlot(int slot) {
        return slot >= 0 && slot < tanks.size();
    }

    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public ItemStack getContainer() {
        return stack;
    }
}
