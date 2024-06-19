package com.sonamorningstar.eternalartifacts.capabilities;

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

public class MultiFluidTank implements IFluidHandler {
    @Setter
    private List<FluidTank> tanks = new ArrayList<>();

    public MultiFluidTank(FluidTank... tanks) {
            this.tanks.addAll(Arrays.asList(tanks));
    }

    public MultiFluidTank readFromNBT(CompoundTag nbt) {
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
        for(IFluidHandler tank : tanks) {
            CompoundTag entry = new CompoundTag();
            entry.putString("FluidName", BuiltInRegistries.FLUID.getKey(tank.getFluidInTank(0).getFluid()).toString());
            entry.putInt("Amount", tank.getFluidInTank(0).getAmount());
            tanksList.add(entry);
        }
        if(nbt != null) nbt.put("Tanks", tanksList);

        return nbt;
    }

    public IFluidHandler get(int tank) {
        return tanks.get(tank);
    }

    public int getEmptyTankCount() {
        int counter = 0;
        for(IFluidHandler tank : tanks) if(tank.getFluidInTank(0).isEmpty()) counter++;
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
        //tanks.sort(new TankComparator());
        for(IFluidHandler tank : tanks) {
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
        //tanks.sort(new TankComparator());
        for(IFluidHandler tank : tanks) {
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
        //tanks.sort(new TankComparator());
        for(IFluidHandler tank : tanks) {
            drained = tank.drain(maxDrain, FluidAction.SIMULATE);
            if(!drained.isEmpty()) {
                drained = tank.drain(maxDrain, action);
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    private static class TankComparator implements Comparator<IFluidHandler> {
        @Override
        public int compare(IFluidHandler o1, IFluidHandler o2) {
            int fluidAmount1 = o1.getFluidInTank(0).getAmount();
            int fluidAmount2 = o2.getFluidInTank(0).getAmount();
            return fluidAmount1 - fluidAmount2;
        }
    }
}
