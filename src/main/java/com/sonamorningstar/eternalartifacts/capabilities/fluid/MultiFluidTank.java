package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

@Setter
public class MultiFluidTank<T extends AbstractFluidTank> extends AbstractFluidTank {
    protected List<T> tanks = new ArrayList<>();

    @SafeVarargs
    public MultiFluidTank(T... tanks) {
            this.tanks.addAll(Arrays.asList(tanks));
    }
    @Override
    public int getCapacity(int tank) {
        return getTankCapacity(tank);
    }

    @Override
    public int getFluidAmount(int tank) {
        return getFluidInTank(tank).getAmount();
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag tanksList = new ListTag();
        for(T tank : tanks) {
            CompoundTag entry = new CompoundTag();
            FluidStack stack = tank.getFluidInTank(0);
            stack.writeToNBT(entry);
            tanksList.add(entry);
        }
        nbt.put("Tanks", tanksList);
        return nbt;
    }
    public void deserializeNBT(CompoundTag nbt) {
        ListTag tanksList = nbt.getList("Tanks", 10);
        for(int i = 0; i < tanksList.size() ; i++) {
            CompoundTag entry = tanksList.getCompound(i);
            FluidStack stack = FluidStack.loadFluidStackFromNBT(entry);
            tanks.get(i).setFluid(stack, 0);
        }
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
    return tanks.get(tank).getFluid(tank);
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
    //region Transfer Methods
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        /*List<Integer> existingTanks = new ArrayList<>();
        for (int i = 0; i < getTanks(); i++) {
            T tank = tanks.get(i);
            Fluid fluid = tank.getFluid(0).getFluid();
            if (fluid.isSame(resource.getFluid())) existingTanks.add(i);
        }
        if (!existingTanks.isEmpty()) {
            int filled;
            for (Integer tankNo : existingTanks) {
                T tank = tanks.get(tankNo);
                filled = tank.fill(resource, FluidAction.SIMULATE);
                if (filled > 0) filled = filled - tank.fill(resource, action);
                if (filled == 0) return 0;
            }
        }*/
        for(T tank : tanks) {
            int filled;
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
    //region Transfer stuff forced
    @Override
    public FluidStack drainForced(int i, FluidAction fluidAction) {
        FluidStack drained;
        for(T tank : tanks) {
            drained = tank.drainForced(i, FluidAction.SIMULATE);
            if(!drained.isEmpty()) {
                drained = tank.drainForced(i, fluidAction);
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drainForced(FluidStack stack, FluidAction fluidAction) {
        FluidStack drained;
        for(T tank : tanks) {
            drained = tank.drainForced(stack, FluidAction.SIMULATE);
            if(!drained.isEmpty()) {
                drained = tank.drainForced(stack, fluidAction);
                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int fillForced(FluidStack stack, FluidAction fluidAction) {
        int filled;
        for(T tank : tanks) {
            filled = tank.fillForced(stack, FluidAction.SIMULATE);
            if(filled > 0) {
                filled = tank.fillForced(stack, fluidAction);
                return filled;
            }
        }
        return 0;
    }
    //endregion

    @Override
    public void setFluid(FluidStack stack, int tank) {
        tanks.get(tank).setFluid(stack, tank);

    }

    @Override
    public FluidStack getFluid(int tank) {
        return getFluidInTank(tank);
    }
}
