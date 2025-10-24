package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

public class ModItemMultiFluidTank<T extends ModFluidStorage> extends MultiFluidTank<T> implements IFluidHandlerItem {
    private final ItemStack stack;

    public ModItemMultiFluidTank(ItemStack stack, List<T> tanks) {
        super(tanks);
        tanks.forEach(tank -> tank.addListener(this::onContentsChange));
        this.stack = stack;
        CompoundTag tag = stack.getTag();
        if (tag != null) deserializeNBT(tag);
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
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
    
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag tanksList = nbt.getList("Tanks", 10);
        for(int i = 0; i < tanksList.size(); i++) {
            CompoundTag entry = tanksList.getCompound(i);
            FluidStack stack = FluidStack.loadFluidStackFromNBT(entry);
            int tankNo = entry.getInt("TankNo");
            if (tankNo < tanks.size()) tanks.get(tankNo).setFluid(stack, 0);
        }
    }

    @Override
    protected void onContentsChange() {
        stack.getOrCreateTag().merge(serializeNBT());
    }
    
    @Override
    public void setTank(int tankNo, FluidStack fluidStack) {
        super.setTank(tankNo, fluidStack);
        onContentsChange();
    }
    
    @Override
    public ItemStack getContainer() {
        return stack;
    }
}
