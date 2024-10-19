package com.sonamorningstar.eternalartifacts.capabilities.fluid;

import com.sonamorningstar.eternalartifacts.content.item.block.base.ICapabilityListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.function.Predicate;

//There already a class exists for this purpose soo idk.
public class ModItemFluidStorage extends ModFluidStorage implements IFluidHandlerItem {
    final ItemStack stack;
    public ModItemFluidStorage(int capacity, ItemStack stack) {
        this(capacity, fs -> true, stack);
    }

    public ModItemFluidStorage(int capacity, Predicate<FluidStack> validator, ItemStack stack) {
        super(capacity, validator);
        this.stack = stack;
        CompoundTag tag = stack.getOrCreateTag().getCompound("Fluid");
        deserializeNBT(tag);
    }

    @Override
    protected void onContentsChanged() {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("Fluid", serializeNBT());
        if(stack.getItem() instanceof ICapabilityListener listener) listener.onFluidContentChange(this.stack);
    }

    @Override
    public ItemStack getContainer() {
        return stack;
    }
}
