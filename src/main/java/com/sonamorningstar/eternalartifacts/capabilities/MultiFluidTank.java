package com.sonamorningstar.eternalartifacts.capabilities;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.*;

@Setter
public class MultiFluidTank<T extends FluidTank> implements IFluidHandler {
    private List<T> tanks = new ArrayList<>();

    @SafeVarargs
    public MultiFluidTank(T... tanks) {
            this.tanks.addAll(Arrays.asList(tanks));
    }

    public MultiFluidTank<?> readFromNBT(CompoundTag nbt) {
        ListTag tanksList = nbt.getList("Tanks", 10);
        for(int i = 0; i < tanksList.size() ; i++) {
            CompoundTag entry = tanksList.getCompound(i);
            Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(entry.getString("FluidName")));
            FluidStack stack;
            if(fluid == Fluids.EMPTY) stack = FluidStack.EMPTY;
            else stack = new FluidStack(fluid, entry.getInt("Amount"));
            tanks.get(i).setFluid(stack);
        }
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        ListTag tanksList = new ListTag();
        for(T tank : tanks) {
            CompoundTag entry = new CompoundTag();
            entry.putString("FluidName", BuiltInRegistries.FLUID.getKey(tank.getFluidInTank(0).getFluid()).toString());
            entry.putInt("Amount", tank.getFluidInTank(0).getAmount());
            tanksList.add(entry);
        }
        if(nbt != null) nbt.put("Tanks", tanksList);

        return nbt;
    }

    public T get(int tank) {
        return tanks.get(tank);
    }

    public List<T> getTanksAsList() { return tanks; }

    public int getEmptyTankCount() {
        int counter = 0;
        for(T tank : tanks) if(tank.getFluidInTank(0).isEmpty()) counter++;
        return counter;
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
    return tanks.get(tank).getFluidInTank(0);
}

    @Override
    public int getTankCapacity(int tank) {
        return tanks.get(tank).getTankCapacity(0);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tanks.get(tank).isFluidValid(0, stack);
    }

    //TODO: Prioritize most filled tank first.
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

}
