package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.container.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.util.QuadFunction;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SidedTransferBlockEntity<T extends AbstractMachineMenu> extends MachineBlockEntity<T>{
    public SidedTransferBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF) {
        super(type, pos, blockState, quadF);
    }
    @Getter
    @Setter
    private Map<Integer, TransferType> sideConfigs = new HashMap<>(6);
    @Getter
    @Setter
    private Map<Integer, Boolean> autoConfigs = new HashMap<>(4);

    protected void performAutoInput(Level lvl, BlockPos pos, IItemHandler inventory) {
        boolean isAllowedAuto = autoConfigs.get(0) != null && autoConfigs.get(0);
        boolean isDisabled = autoConfigs.get(2) != null && autoConfigs.get(2);
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
            if(be != null) {
                IItemHandler sourceInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
                if(sourceInv != null) {
                    for(int i = 0; i < sourceInv.getSlots(); i++) {
                        if(sourceInv.getStackInSlot(i).isEmpty()) continue;
                        ItemStack inserted = ItemHandlerHelper.insertItemStacked(inventory, sourceInv.getStackInSlot(i), true);
                        if(inserted.isEmpty()) {
                            ItemHandlerHelper.insertItemStacked(inventory, sourceInv.getStackInSlot(i).copyWithCount(sourceInv.getStackInSlot(i).getCount()), false);
                            sourceInv.extractItem(i, sourceInv.getStackInSlot(i).getCount(), false);
                        }
                    }
                }
            }
        }
    }

    protected void performAutoOutput(Level lvl, BlockPos pos, IItemHandler inventory, int... outputSlots) {
        boolean isAllowedAuto = autoConfigs.get(1) != null && autoConfigs.get(1);
        boolean isDisabled = autoConfigs.get(2) != null && autoConfigs.get(2);
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
            if(be != null) {
                IItemHandler targetInv = lvl.getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
                if(targetInv != null) {
                    for(int output : outputSlots) {
                        ItemStack inserted = ItemHandlerHelper.insertItemStacked(targetInv, inventory.getStackInSlot(output), true);
                        if(inserted.isEmpty()) {
                            ItemHandlerHelper.insertItemStacked(targetInv, inventory.getStackInSlot(output), false);
                            inventory.extractItem(output, inventory.getStackInSlot(output).getCount(), false);
                        }
                    }
                }
            }
        }
    }

    protected void performAutoInputFluids(Level lvl, BlockPos pos, IFluidHandler tank) {
        boolean isAllowedAuto = autoConfigs.get(0) != null && autoConfigs.get(0);
        boolean isDisabled = autoConfigs.get(3) != null && autoConfigs.get(3);
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
            if(be != null) {
                IFluidHandler sourceTank = lvl.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
                if(sourceTank != null) {
                    FluidUtil.tryFluidTransfer(tank, sourceTank, 1000, true);
                }
            }
        }
    }

    protected void performAutoOutputFluids(Level lvl, BlockPos pos, IFluidHandler tank) {
        boolean isAllowedAuto = autoConfigs.get(1) != null && autoConfigs.get(1);
        boolean isDisabled = autoConfigs.get(3) != null && autoConfigs.get(3);
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
            if(be != null) {
                IFluidHandler targetTank = lvl.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
                if(targetTank != null) {
                    FluidUtil.tryFluidTransfer(targetTank, tank, 1000, true);
                }
            }
        }
    }

    protected void performAutoInputEnergy(Level lvl, BlockPos pos, IEnergyStorage energy) {
        boolean isAllowedAuto = autoConfigs.get(0) != null && autoConfigs.get(0);
        if(!isAllowedAuto) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
            if(be != null) {
                IEnergyStorage target = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
                if(target != null && target.canExtract()) {
                    int received = target.extractEnergy(Math.min(energy.getMaxEnergyStored() - energy.getEnergyStored(), target.getEnergyStored()), true);
                    if(received > 0) {
                        energy.receiveEnergy(received, false);
                        target.extractEnergy(received, false);
                    }
                }
            }
        }
    }

    protected void performAutoOutputEnergy(Level lvl, BlockPos pos, IEnergyStorage energy) {
        boolean isAllowedAuto = autoConfigs.get(1) != null && autoConfigs.get(1);
        if(!isAllowedAuto) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            BlockEntity be = lvl.getBlockEntity(pos.relative(dir));
            if(be != null) {
                IEnergyStorage target = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), be.getBlockState(), be, dir.getOpposite());
                if(target != null && target.canReceive()) {
                    int extracted = target.receiveEnergy(Math.min(energy.getEnergyStored(), target.getMaxEnergyStored() - target.getEnergyStored()), true);
                    if(extracted > 0) {
                        target.receiveEnergy(extracted, false);
                        energy.extractEnergy(extracted, false);
                    }
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ListTag sideConfigs = tag.getList("SideConfigs", Tag.TAG_COMPOUND);
        for(int i = 0; i < sideConfigs.size(); i++) {
            CompoundTag entry = sideConfigs.getCompound(i);
            this.sideConfigs.put(entry.getInt("Index"), TransferType.valueOf(entry.getString("Type")));
        }
        ListTag autoConfigs = tag.getList("AutoConfigs", Tag.TAG_COMPOUND);
        for(int i = 0; i < autoConfigs.size(); i++) {
            CompoundTag entry = autoConfigs.getCompound(i);
            this.autoConfigs.put(entry.getInt("Index"), entry.getBoolean("Enabled"));
        }
        ListTag redstoneConfigs = tag.getList("RedstoneConfigs", Tag.TAG_COMPOUND);
        for(int i = 0; i < redstoneConfigs.size(); i++) {
            CompoundTag entry = redstoneConfigs.getCompound(i);
            this.redstoneConfigs.put(entry.getInt("Index"), RedstoneType.valueOf(entry.getString("Type")));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag sideConfigs = new ListTag();
        this.sideConfigs.forEach((k, v) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("Index", k);
            entry.putString("Type", v.toString());
            sideConfigs.add(entry);
        });
        tag.put("SideConfigs", sideConfigs);
        ListTag autoConfigs = new ListTag();
        this.autoConfigs.forEach((k, v) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("Index", k);
            entry.putBoolean("Enabled", v);
            autoConfigs.add(entry);
        });
        tag.put("AutoConfigs", autoConfigs);
        ListTag redstoneConfigs = new ListTag();
        this.redstoneConfigs.forEach((k, v) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("Index", k);
            entry.putString("Type", v.toString());
            redstoneConfigs.add(entry);
        });
        tag.put("RedstoneConfigs", redstoneConfigs);
    }

    public static boolean canPerformTransfer(SidedTransferBlockEntity<?> be, Direction dir, TransferType wanted) {
        List<Direction> available = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            SidedTransferBlockEntity.TransferType type = be.sideConfigs.get(i) == null ? SidedTransferBlockEntity.TransferType.DEFAULT : be.sideConfigs.get(i);
            if(type == wanted) available.add(SidedTransferBlockEntity.resolveActualDir(be.getBlockState(), i));
        }
        return available.contains(dir);
    }

    public static boolean canPerformTransfers(SidedTransferBlockEntity<?> be, Direction dir, TransferType... wanted) {
        for(TransferType type : wanted) {
            if(canPerformTransfer(be, dir, type)) return true;
        }
        return false;
    }

    public boolean isItemsAllowed() {
        return autoConfigs.get(2) == null || !autoConfigs.get(2);
    }

    public boolean isFluidsAllowed() {
        return autoConfigs.get(3) == null || !autoConfigs.get(3);
    }

    public static Direction resolveActualDir(BlockState state, int index) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return switch (index) {
            case 0 -> Direction.UP;
            case 1 -> facing.getClockWise();
            case 2 -> facing;
            case 3 -> facing.getCounterClockWise();
            case 4 -> Direction.DOWN;
            case 5 -> facing.getOpposite();
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
    }

    public enum TransferType {
        DEFAULT,
        NONE,
        PULL,
        PUSH;

        public static TransferType cycleNext(int index, SidedTransferBlockEntity<?> entity) {
            TransferType type = entity.sideConfigs.get(index) == null ? DEFAULT : entity.sideConfigs.get(index);
            if(type == DEFAULT) return NONE;
            if(type == NONE) return PULL;
            if(type == PULL) return PUSH;
            return DEFAULT;
        }

        public static TransferType cyclePrev(int index, SidedTransferBlockEntity<?> entity) {
            TransferType type = entity.sideConfigs.get(index) == null ? DEFAULT : entity.sideConfigs.get(index);
            if(type == DEFAULT) return PUSH;
            if(type == NONE) return DEFAULT;
            if(type == PULL) return NONE;
            return PULL;
        }
    }

    public enum RedstoneType {
        IGNORED,
        HIGH,
        LOW;

        public static RedstoneType cycleNext(int index, SidedTransferBlockEntity<?> entity) {
            RedstoneType type = entity.redstoneConfigs.get(index) == null ? IGNORED : entity.redstoneConfigs.get(index);
            if(type == IGNORED) return HIGH;
            if(type == HIGH) return LOW;
            return IGNORED;
        }

        public static RedstoneType cyclePrev(int index, SidedTransferBlockEntity<?> entity) {
            RedstoneType type = entity.redstoneConfigs.get(index) == null ? IGNORED : entity.redstoneConfigs.get(index);
            if(type == IGNORED) return LOW;
            if(type == HIGH) return IGNORED;
            return HIGH;
        }
    }
}
