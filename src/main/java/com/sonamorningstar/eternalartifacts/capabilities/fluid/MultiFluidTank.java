package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;

@Setter
public class MultiFluidTank<T extends AbstractFluidTank> extends AbstractFluidTank {
    protected final List<T> tanks;

    @SafeVarargs
    public MultiFluidTank(T... tanks) {
            this.tanks = Arrays.asList(tanks);
    }
    public MultiFluidTank(List<T> tanks) {
        this.tanks = tanks;
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

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
    return tanks.get(tank).getFluid(0);
}

    @Override
    public int getTankCapacity(int tank) {
        return tanks.get(tank).getTankCapacity(0);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tanks.get(tank).isFluidValid(0, stack);
    }

    //region Transfer Methods
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        
        FluidStack resourceCopy = resource.copy();
        int totalFilled = 0;
        
        List<T> compatibleTanks = new ArrayList<>();
        for (T tank : tanks) {
            FluidStack tankStack = tank.getFluid(0);
            if (!tankStack.isEmpty() && tankStack.isFluidEqual(resourceCopy) && tank.isFluidValid(0, resourceCopy)) {
                compatibleTanks.add(tank);
            }
        }
        
        compatibleTanks.sort((t1, t2) -> {
            double fillRate1 = (double) t1.getFluidAmount(0) / t1.getTankCapacity(0);
            double fillRate2 = (double) t2.getFluidAmount(0) / t2.getTankCapacity(0);
            return Double.compare(fillRate2, fillRate1);
        });
        
        for (T tank : compatibleTanks) {
            int filled = tank.fill(resourceCopy, action);
            totalFilled += filled;
            resourceCopy.shrink(filled);
            
            if (resourceCopy.isEmpty()) return totalFilled;
        }
        
        for (T tank : tanks) {
            if (tank.getFluid(0).isEmpty() && tank.isFluidValid(0, resourceCopy)) {
                int filled = tank.fill(resourceCopy, action);
                totalFilled += filled;
                resourceCopy.shrink(filled);
                
                if (resourceCopy.isEmpty()) return totalFilled;
            }
        }
        
        return totalFilled;
    }
    
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        
        FluidStack resourceCopy = resource.copy();
        FluidStack result = FluidStack.EMPTY;
        
        List<T> compatibleTanks = new ArrayList<>();
        for (T tank : tanks) {
            FluidStack tankStack = tank.getFluid(0);
            if (!tankStack.isEmpty() && tankStack.isFluidEqual(resourceCopy)) {
                compatibleTanks.add(tank);
            }
        }
        
        compatibleTanks.sort((t1, t2) -> {
            double fillRate1 = (double) t1.getFluidAmount(0) / t1.getTankCapacity(0);
            double fillRate2 = (double) t2.getFluidAmount(0) / t2.getTankCapacity(0);
            return Double.compare(fillRate1, fillRate2);
        });
        
        for (T tank : compatibleTanks) {
            FluidStack drained = tank.drain(resourceCopy, action);
            
            if (!drained.isEmpty()) {
                resourceCopy.shrink(drained.getAmount());
                
                if (result.isEmpty()) result = drained;
                else result.grow(drained.getAmount());
                
                if (resourceCopy.isEmpty()) return result;
                
            }
        }
        
        return result;
    }
    
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0) return FluidStack.EMPTY;
        
        FluidStack targetFluid = null;
        List<T> compatibleTanks = new ArrayList<>();
        
        for (T tank : tanks) {
            FluidStack fluid = tank.getFluid(0);
            if (!fluid.isEmpty()) {
                FluidStack simulatedDrain = tank.drain(maxDrain, FluidAction.SIMULATE);
                if (!simulatedDrain.isEmpty()) {
                    if (targetFluid == null) {
                        targetFluid = simulatedDrain.copy();
                        compatibleTanks.add(tank);
                    } else if (simulatedDrain.isFluidEqual(targetFluid)) {
                        compatibleTanks.add(tank);
                    }
                }
            }
        }
        
        if (targetFluid == null) return FluidStack.EMPTY;
        
        compatibleTanks.sort((t1, t2) -> {
            double fillRate1 = (double) t1.getFluidAmount(0) / t1.getTankCapacity(0);
            double fillRate2 = (double) t2.getFluidAmount(0) / t2.getTankCapacity(0);
            return Double.compare(fillRate1, fillRate2);
        });
        
        int remaining = maxDrain;
        FluidStack result = FluidStack.EMPTY;
        
        for (T tank : compatibleTanks) {
            FluidStack drained = tank.drain(remaining, action);
            
            if (!drained.isEmpty()) {
                remaining -= drained.getAmount();
                
                if (result.isEmpty()) result = drained;
                else result.grow(drained.getAmount());
                
                if (remaining <= 0) break;
            }
        }
        
        return result;
    }
    //endregion
    //region Transfer stuff forced
    @Override
    public FluidStack drainForced(int maxDrain, FluidAction action) {
        if (maxDrain <= 0) return FluidStack.EMPTY;
        
        FluidStack targetFluid = null;
        List<T> compatibleTanks = new ArrayList<>();
        
        for (T tank : tanks) {
            FluidStack fluid = tank.getFluid(0);
            if (!fluid.isEmpty()) {
                FluidStack simulatedDrain = tank.drainForced(maxDrain, FluidAction.SIMULATE);
                if (!simulatedDrain.isEmpty()) {
                    if (targetFluid == null) {
                        targetFluid = simulatedDrain.copy();
                        compatibleTanks.add(tank);
                    } else if (simulatedDrain.isFluidEqual(targetFluid)) {
                        compatibleTanks.add(tank);
                    }
                }
            }
        }
        
        if (targetFluid == null) return FluidStack.EMPTY;
        
        compatibleTanks.sort((t1, t2) -> {
            double fillRate1 = (double) t1.getFluidAmount(0) / t1.getTankCapacity(0);
            double fillRate2 = (double) t2.getFluidAmount(0) / t2.getTankCapacity(0);
            return Double.compare(fillRate1, fillRate2);
        });
        
        int remaining = maxDrain;
        FluidStack result = FluidStack.EMPTY;
        
        for (T tank : compatibleTanks) {
            FluidStack drained = tank.drainForced(remaining, action);
            
            if (!drained.isEmpty()) {
                remaining -= drained.getAmount();
                
                if (result.isEmpty()) result = drained;
                else result.grow(drained.getAmount());
                
                if (remaining <= 0) break;
            }
        }
        
        return result;
    }
    
    @Override
    public FluidStack drainForced(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        
        FluidStack resourceCopy = resource.copy();
        FluidStack result = FluidStack.EMPTY;
        
        List<T> compatibleTanks = new ArrayList<>();
        for (T tank : tanks) {
            FluidStack tankStack = tank.getFluid(0);
            if (!tankStack.isEmpty() && tankStack.isFluidEqual(resourceCopy)) {
                compatibleTanks.add(tank);
            }
        }
        
        compatibleTanks.sort((t1, t2) -> {
            double fillRate1 = (double) t1.getFluidAmount(0) / t1.getTankCapacity(0);
            double fillRate2 = (double) t2.getFluidAmount(0) / t2.getTankCapacity(0);
            return Double.compare(fillRate1, fillRate2);
        });
        
        for (T tank : compatibleTanks) {
            FluidStack drained = tank.drainForced(resourceCopy, action);
            
            if (!drained.isEmpty()) {
                resourceCopy.shrink(drained.getAmount());
                
                if (result.isEmpty()) result = drained;
                else result.grow(drained.getAmount());
                
                if (resourceCopy.isEmpty()) return result;
                
            }
        }
        
        return result;
    }


    @Override
    public int fillForced(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        
        FluidStack resourceCopy = resource.copy();
        int totalFilled = 0;
        
        List<T> compatibleTanks = new ArrayList<>();
        for (T tank : tanks) {
            FluidStack tankStack = tank.getFluid(0);
            if (!tankStack.isEmpty() && tankStack.isFluidEqual(resourceCopy) && tank.isFluidValid(0, resourceCopy)) {
                compatibleTanks.add(tank);
            }
        }
        
        compatibleTanks.sort((t1, t2) -> {
            double fillRate1 = (double) t1.getFluidAmount(0) / t1.getTankCapacity(0);
            double fillRate2 = (double) t2.getFluidAmount(0) / t2.getTankCapacity(0);
            return Double.compare(fillRate2, fillRate1);
        });
        
        for (T tank : compatibleTanks) {
            int filled = tank.fillForced(resourceCopy, action);
            totalFilled += filled;
            resourceCopy.shrink(filled);
            
            if (resourceCopy.isEmpty()) {
                return totalFilled;
            }
        }
        
        for (T tank : tanks) {
            if (tank.getFluid(0).isEmpty() && tank.isFluidValid(0, resourceCopy)) {
                int filled = tank.fillForced(resourceCopy, action);
                totalFilled += filled;
                resourceCopy.shrink(filled);
                
                if (resourceCopy.isEmpty()) {
                    return totalFilled;
                }
            }
        }
        
        return totalFilled;
    }
    //endregion
    
    public T getTank(int tankNo) {
        return tanks.get(tankNo);
    }
    
    public void setTank(int tankNo, FluidStack fluidStack) {
        tanks.get(tankNo).setFluid(fluidStack, 0);
    }
    
    @Override
    public void setFluid(FluidStack stack, int tank) {
        tanks.get(tank).setFluid(stack, 0);
    }

    @Override
    public FluidStack getFluid(int tank) {
        return getFluidInTank(tank);
    }
}
