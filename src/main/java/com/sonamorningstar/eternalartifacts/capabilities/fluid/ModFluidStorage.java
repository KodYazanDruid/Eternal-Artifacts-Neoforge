package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ModFluidStorage extends AbstractFluidTank {
    protected Predicate<FluidStack> validator;
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;
    private final List<Runnable> listeners = new ArrayList<>();

    public ModFluidStorage(int capacity) {
        this(capacity, e -> true);
    }

    public ModFluidStorage(int capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    //region Transfer stuff forced.
    public int fillForced(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() /*|| !isFluidValid(resource)*/) {
            return 0;
        }
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @Override
    public int getCapacity(int tank) {
        return capacity;
    }

    @Override
    public int getFluidAmount(int tank) {
        return getFluidAmount();
    }

    @Override
    public AbstractFluidTank get(int i) {
        return this;
    }

    public FluidStack drainForced(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.EMPTY;
        }
        return drainForced(resource.getAmount(), action);
    }

    public FluidStack drainForced(int maxDrain, FluidAction action) {
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }
    //endregion

    //region Transfer stuff
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }
    //endregion

    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    public int getFluidAmount() {
        return fluid.getAmount();
    }

    @Override
    public void setFluid(FluidStack stack, int tank) {
        this.fluid = stack;
        onContentsChanged();
    }
    
    @Override
    public void setFluidSilent(FluidStack stack, int tank) {
        this.fluid = stack;
    }
    
    @Override
    public FluidStack getFluid(int tank) {
        return getFluidInTank(tank);
    }

    public void deserializeNBT(CompoundTag nbt) {
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
        setFluid(fluid, 0);
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        fluid.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return isFluidValid(stack);
    }

    protected void onContentsChanged() {
        listeners.forEach(Runnable::run);
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }
}
