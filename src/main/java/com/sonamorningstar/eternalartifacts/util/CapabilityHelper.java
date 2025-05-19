package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.energy.WrappedEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.WrappedFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemItemStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.WrappedItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.List;

public class CapabilityHelper {

    @Contract("_, _, null, _ -> param2")
    public static @Nullable IItemHandlerModifiable regSidedItemCaps(SidedTransferMachine<?> be, IItemHandlerModifiable inventory, Direction ctx, @Nullable List<Integer> outputSlots) {
        if (ctx != null) {
            if(SidedTransferMachine.canPerformTransfer(be, ctx, SidedTransferMachine.TransferType.NONE) || !be.areItemsAllowed()) return null;
            return new WrappedItemStorage(inventory,
                    i -> (outputSlots != null && outputSlots.contains(i)) &&
                            SidedTransferMachine.canPerformTransfers(be, ctx, SidedTransferMachine.TransferType.PUSH, SidedTransferMachine.TransferType.DEFAULT) &&
                            be.areItemsAllowed(),
                    (i, s) -> (outputSlots == null || !outputSlots.contains(i)) &&
                            SidedTransferMachine.canPerformTransfers(be ,ctx, SidedTransferMachine.TransferType.PULL, SidedTransferMachine.TransferType.DEFAULT) &&
                            be.areItemsAllowed());
        } else return inventory;
    }

    @Contract("_, _, null -> param2")
    public static @Nullable IFluidHandler regSidedFluidCaps(SidedTransferMachine<?> be, IFluidHandler tank, Direction ctx) {
        if(ctx != null) {
            if(SidedTransferMachine.canPerformTransfer(be, ctx, SidedTransferMachine.TransferType.NONE) || !be.areFluidsAllowed()) return null;
            return new WrappedFluidStorage(tank,
                    dir -> SidedTransferMachine.canPerformTransfers(be, dir, SidedTransferMachine.TransferType.PUSH, SidedTransferMachine.TransferType.DEFAULT) &&
                            be.areFluidsAllowed(),
                    (dir, fs) -> SidedTransferMachine.canPerformTransfers(be, dir, SidedTransferMachine.TransferType.PULL, SidedTransferMachine.TransferType.DEFAULT) &&
                            be.areFluidsAllowed(),
                    ctx);
        } else return tank;
    }

    @Contract("_, _, null -> param2")
    public static @Nullable IEnergyStorage regSidedEnergyCaps(SidedTransferMachine<?> be, IEnergyStorage energy, Direction ctx) {
        if(ctx != null) {
            if(SidedTransferMachine.canPerformTransfer(be, ctx, SidedTransferMachine.TransferType.NONE)) return null;
            return new WrappedEnergyStorage(energy,
                    dir -> SidedTransferMachine.canPerformTransfers(be, dir, SidedTransferMachine.TransferType.PUSH, SidedTransferMachine.TransferType.DEFAULT),
                    dir -> SidedTransferMachine.canPerformTransfers(be, dir, SidedTransferMachine.TransferType.PULL, SidedTransferMachine.TransferType.DEFAULT),
                    ctx);
        }else return energy;
    }

    public static ModItemItemStorage regItemItemCap(ItemStack stack, int baseSlots) {
        return new ModItemItemStorage(stack, baseSlots);
    }

    public static ModItemEnergyStorage regItemEnergyCap(ItemStack stack, int baseCapability, int maxTransfer) {
        int volumeLevel = stack.getEnchantmentLevel(ModEnchantments.VOLUME.get());
        int capacity = (volumeLevel + 1) * baseCapability;
        int transfer = (volumeLevel + 1) * maxTransfer;
        return new ModItemEnergyStorage(capacity, transfer, stack);
    }

    public static IFluidHandlerItem regItemFluidCap(ItemStack stack, int baseCapacity) {
        int volumeLevel = stack.getEnchantmentLevel(ModEnchantments.VOLUME.get());
        int capacity = (volumeLevel + 1) * baseCapacity;
        return new FluidHandlerItemStack(stack, capacity);
    }
}
