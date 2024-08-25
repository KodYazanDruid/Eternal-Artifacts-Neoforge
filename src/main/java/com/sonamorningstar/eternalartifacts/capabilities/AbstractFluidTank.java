package com.sonamorningstar.eternalartifacts.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFluidTank implements IFluidHandler, INBTSerializable<CompoundTag> {

    public abstract void setFluid(FluidStack stack, int tank);
    public abstract FluidStack getFluid(int tank);

    public abstract CompoundTag serializeNBT();
    public abstract void deserializeNBT(CompoundTag tag);

    public abstract FluidStack drainForced(int i, FluidAction fluidAction);
    public abstract FluidStack drainForced(FluidStack stack, FluidAction fluidAction);
    public abstract int fillForced(FluidStack stack, FluidAction fluidAction);

    public abstract int getCapacity(int tank);
    public abstract int getFluidAmount(int tank);

    public abstract AbstractFluidTank get(int i);

    public List<FluidStack> toList() {
        List<FluidStack> fluids = new ArrayList<>();
        for(int i = 0; i < this.getTanks(); i++) {
            fluids.add(getFluidInTank(i));
        }
        return fluids;
    }
}
