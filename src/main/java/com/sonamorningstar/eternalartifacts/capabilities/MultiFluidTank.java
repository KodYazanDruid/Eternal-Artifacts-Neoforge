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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiFluidTank implements IFluidHandler {
    @Setter
    private List<ModFluidStorage> tanks = new ArrayList<>();

    public MultiFluidTank(ModFluidStorage... tanks) {
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
        for(ModFluidStorage tank : tanks) {
            CompoundTag entry = new CompoundTag();
            entry.putString("FluidName", BuiltInRegistries.FLUID.getKey(tank.getFluid().getFluid()).toString());
            entry.putInt("Amount", tank.getFluidAmount());
            tanksList.add(entry);
        }
        if(nbt != null) nbt.put("Tanks", tanksList);

        return nbt;
    }

    public ModFluidStorage get(int tank) {
        return tanks.get(tank);
    }

    @Override
    public int getTanks() {
        return tanks.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tanks.get(tank).getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return tanks.get(tank).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tanks.get(tank).isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled;
        for(ModFluidStorage tank : tanks) {
             filled = tank.fill(resource, action);
            if(filled > 0) return filled;
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained;
        for(ModFluidStorage tank : tanks) {
            drained = tank.drain(resource, action);
            if(!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained;
        for(ModFluidStorage tank : tanks) {
            drained = tank.drain(maxDrain, action);
            if(!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }
}
