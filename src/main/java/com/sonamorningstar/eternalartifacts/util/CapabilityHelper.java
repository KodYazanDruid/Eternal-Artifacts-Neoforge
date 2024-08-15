package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.capabilities.WrappedEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.WrappedFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.WrappedItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.List;

public class CapabilityHelper {

    @Contract("_, _, null, _ -> param2")
    public static @org.jetbrains.annotations.Nullable IItemHandlerModifiable regSidedItemCaps(SidedTransferMachineBlockEntity<?> be, IItemHandlerModifiable inventory, Direction ctx, @Nullable List<Integer> outputSlots) {
        if (ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferMachineBlockEntity.canPerformTransfer(be, ctx, SidedTransferMachineBlockEntity.TransferType.NONE) || !be.isItemsAllowed()) return null;
            return new WrappedItemStorage(inventory,
                    i -> (outputSlots != null && outputSlots.contains(i)) &&
                            SidedTransferMachineBlockEntity.canPerformTransfers(be, ctx, SidedTransferMachineBlockEntity.TransferType.PUSH, SidedTransferMachineBlockEntity.TransferType.DEFAULT) &&
                            be.isItemsAllowed(),
                    (i, s) -> (outputSlots == null || !outputSlots.contains(i)) &&
                            SidedTransferMachineBlockEntity.canPerformTransfers(be ,ctx, SidedTransferMachineBlockEntity.TransferType.PULL, SidedTransferMachineBlockEntity.TransferType.DEFAULT) &&
                            be.isItemsAllowed());
        } else return inventory;
    }

    @Contract("_, _, null -> param2")
    public static @org.jetbrains.annotations.Nullable IFluidHandler regSidedFluidCaps(SidedTransferMachineBlockEntity<?> be, IFluidHandler tank, Direction ctx) {
        if(ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferMachineBlockEntity.canPerformTransfer(be, ctx, SidedTransferMachineBlockEntity.TransferType.NONE) || !be.isFluidsAllowed()) return null;
            return new WrappedFluidStorage(tank,
                    dir -> SidedTransferMachineBlockEntity.canPerformTransfers(be, dir, SidedTransferMachineBlockEntity.TransferType.PUSH, SidedTransferMachineBlockEntity.TransferType.DEFAULT) &&
                            be.isFluidsAllowed(),
                    (dir, fs) -> SidedTransferMachineBlockEntity.canPerformTransfers(be, dir, SidedTransferMachineBlockEntity.TransferType.PULL, SidedTransferMachineBlockEntity.TransferType.DEFAULT) &&
                            be.isFluidsAllowed(),
                    ctx);
        } else return tank;
    }

    @Contract("_, _, null -> param2")
    public static @org.jetbrains.annotations.Nullable IEnergyStorage regSidedEnergyCaps(SidedTransferMachineBlockEntity<?> be, IEnergyStorage energy, Direction ctx) {
        if(ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferMachineBlockEntity.canPerformTransfer(be, ctx, SidedTransferMachineBlockEntity.TransferType.NONE)) return null;
            return new WrappedEnergyStorage(energy,
                    dir -> SidedTransferMachineBlockEntity.canPerformTransfers(be, dir, SidedTransferMachineBlockEntity.TransferType.PUSH, SidedTransferMachineBlockEntity.TransferType.DEFAULT),
                    dir -> SidedTransferMachineBlockEntity.canPerformTransfers(be, dir, SidedTransferMachineBlockEntity.TransferType.PULL, SidedTransferMachineBlockEntity.TransferType.DEFAULT),
                    ctx);
        }else return energy;
    }
}
