package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.IHasInventory;
import com.sonamorningstar.eternalartifacts.capabilities.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
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
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SidedTransferBlockEntity<T extends AbstractMachineMenu> extends MachineBlockEntity<T>{
    public SidedTransferBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF) {
        super(type, pos, blockState, quadF);
    }
    @Getter
    @Setter
    private Map<Integer, TransferType> sideConfigs = new HashMap<>(6);
    @Getter
    @Setter
    private Map<Integer, Boolean> autoConfigs = new HashMap<>(2);

    @Override
    public void tick(Level lvl, BlockPos pos, BlockState st) {

    }

    protected void performAutoInput(Level lvl, BlockPos pos, ModItemStorage inventory) {
        boolean isAllowed = autoConfigs.get(0) != null && autoConfigs.get(0);
        if(!isAllowed) return;
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

    protected void performAutoOutput(Level lvl, BlockPos pos, ModItemStorage inventory, int... outputSlots) {
        boolean isAllowed = autoConfigs.get(1) != null && autoConfigs.get(1);
        if(!isAllowed) return;
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

    protected void performAutoInputFluids(Level lvl, BlockPos pos, ModFluidStorage tank) {
        boolean isAllowed = autoConfigs.get(0) != null && autoConfigs.get(0);
        if(!isAllowed) return;
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

    protected void performAutoOutputFluids(Level lvl, BlockPos pos, ModFluidStorage tank) {
        boolean isAllowed = autoConfigs.get(1) != null && autoConfigs.get(1);
        if(!isAllowed) return;
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
        List<Direction> available = new ArrayList<>();
        for(TransferType type : wanted) {
            if(canPerformTransfer(be, dir, type)) return true;
        }
        return false;
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

    // 0 -> top
    // 1 -> left
    // 2 -> front
    // 3 -> right
    // 4 -> bottom
    // 5 -> back
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
}
