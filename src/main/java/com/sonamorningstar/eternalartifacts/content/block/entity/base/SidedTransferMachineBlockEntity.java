package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineSixWayBlock;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public abstract class SidedTransferMachineBlockEntity<T extends AbstractMachineMenu> extends MachineBlockEntity<T> {
    public SidedTransferMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF) {
        super(type, pos, blockState, quadF);
    }
    private Map<Integer, TransferType> sideConfigs = new HashMap<>(6);
    private Map<Integer, Boolean> autoConfigs = new HashMap<>(4);
    private final MachineConfiguration configuration = new MachineConfiguration(this);

    //region Transfers
    protected void performAutoInputItems(Level lvl, BlockPos pos) {
        boolean isAllowedAuto = autoConfigs.get(0) != null && autoConfigs.get(0);
        boolean isDisabled = autoConfigs.get(2) != null && autoConfigs.get(2);
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            insertItemFromDir(lvl, pos, dir, inventory);
        }
    }

    protected void performAutoOutputItems(Level lvl, BlockPos pos) {
        boolean isAllowedAuto = autoConfigs.get(1) != null && autoConfigs.get(1);
        boolean isDisabled = autoConfigs.get(2) != null && autoConfigs.get(2);
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            outputItemToDir(lvl, pos, dir, inventory);
        }
    }

    protected void performAutoInputFluids(Level lvl, BlockPos pos) {
        boolean isAllowedAuto = autoConfigs.get(0) != null && autoConfigs.get(0);
        boolean isDisabled = autoConfigs.get(3) != null && autoConfigs.get(3);
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            inputFluidFromDir(lvl, pos, dir, tank);
        }
    }

    protected void performAutoOutputFluids(Level lvl, BlockPos pos) {
        boolean isAllowedAuto = autoConfigs.get(1) != null && autoConfigs.get(1);
        boolean isDisabled = autoConfigs.get(3) != null && autoConfigs.get(3);
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            outputFluidToDir(lvl, pos, dir, tank);
        }
    }

    protected void performAutoInputEnergy(Level lvl, BlockPos pos) {
        boolean isAllowedAuto = autoConfigs.get(0) != null && autoConfigs.get(0);
        if(!isAllowedAuto) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            inputEnergyToDir(lvl, pos, dir, energy);
        }
    }

    protected void performAutoOutputEnergy(Level lvl, BlockPos pos) {
        boolean isAllowedAuto = autoConfigs.get(1) != null && autoConfigs.get(1);
        if(!isAllowedAuto) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            TransferType type = sideConfigs.get(i) == null ? TransferType.DEFAULT : sideConfigs.get(i);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            outputEnergyToDir(lvl, pos, dir, energy);
        }
    }
    //endregion

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ListTag sideConfigs = tag.getList("SideConfigs", Tag.TAG_COMPOUND);
        for(int i = 0; i < sideConfigs.size(); i++) {
            CompoundTag entry = sideConfigs.getCompound(i);
            this.sideConfigs.put(entry.getInt("Index"), TransferType.valueOf(entry.getString("Type")));
        }
        for (int i = 0; i < 6; i++) {
            this.sideConfigs.putIfAbsent(i, TransferType.DEFAULT);
        }
        ListTag autoConfigs = tag.getList("AutoConfigs", Tag.TAG_COMPOUND);
        for(int i = 0; i < autoConfigs.size(); i++) {
            CompoundTag entry = autoConfigs.getCompound(i);
            this.autoConfigs.put(entry.getInt("Index"), entry.getBoolean("Enabled"));
        }
        for (int i = 0; i < 4; i++) {
            this.autoConfigs.putIfAbsent(i, false);
        }
        ListTag redstoneConfigs = tag.getList("RedstoneConfigs", Tag.TAG_COMPOUND);
        for(int i = 0; i < redstoneConfigs.size(); i++) {
            CompoundTag entry = redstoneConfigs.getCompound(i);
            this.redstoneConfigs.put(entry.getInt("Index"), RedstoneType.valueOf(entry.getString("Type")));
        }
        this.redstoneConfigs.putIfAbsent(0, RedstoneType.IGNORED);
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
        for (int i = 0; i < 6; i++) {
            this.sideConfigs.putIfAbsent(i, TransferType.DEFAULT);
        }
        for (int i = 0; i < 4; i++) {
            this.autoConfigs.putIfAbsent(i, false);
        }
        this.redstoneConfigs.putIfAbsent(0, RedstoneType.IGNORED);
        /*ListTag sideConfigs = new ListTag();
        this.sideConfigs.forEach((k, v) -> {
            if (v != TransferType.DEFAULT) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putString("Type", v.toString());
                sideConfigs.add(entry);
            }
        });
        tag.put("SideConfigs", sideConfigs);
        ListTag autoConfigs = new ListTag();
        this.autoConfigs.forEach((k, v) -> {
            if (v) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putBoolean("Enabled", v);
                autoConfigs.add(entry);
            }
        });
        tag.put("AutoConfigs", autoConfigs);
        ListTag redstoneConfigs = new ListTag();
        this.redstoneConfigs.forEach((k, v) -> {
            if (v != RedstoneType.IGNORED) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putString("Type", v.toString());
                redstoneConfigs.add(entry);
            }
        });
        tag.put("RedstoneConfigs", redstoneConfigs);*/
    }
    
    /**
     * Method used to load machine data when a machine that was mined with a wrench is placed again.
     * Reads machine data from an NBT tag and saves it to the dropped machine item.
     * This allows the machine's contents (e.g., items, fluids, energy) to be preserved during relocation.
     *
     * @param additionalTag The NBT tag containing the machine contents to load
     */
    @Override
    public void loadContents(CompoundTag additionalTag) {
        super.loadContents(additionalTag);
        //configuration.deserializeNBT(additionalTag.getCompound("SidedTransferConfigs"));
        CompoundTag tag = additionalTag.getCompound("SidedTransferConfigs");
        ListTag sideConfigs = tag.getList("SideConfigs", Tag.TAG_COMPOUND);
        this.sideConfigs = new HashMap<>(6);
        for(int i = 0; i < sideConfigs.size(); i++) {
            CompoundTag entry = sideConfigs.getCompound(i);
            this.sideConfigs.put(entry.getInt("Index"), TransferType.valueOf(entry.getString("Type")));
        }
        for (int i = 0; i < 6; i++) {
            this.sideConfigs.putIfAbsent(i, TransferType.DEFAULT);
        }
        ListTag autoConfigs = tag.getList("AutoConfigs", Tag.TAG_COMPOUND);
        this.autoConfigs = new HashMap<>(4);
        for(int i = 0; i < autoConfigs.size(); i++) {
            CompoundTag entry = autoConfigs.getCompound(i);
            this.autoConfigs.put(entry.getInt("Index"), entry.getBoolean("Enabled"));
        }
        for (int i = 0; i < 4; i++) {
            this.autoConfigs.putIfAbsent(i, false);
        }
        ListTag redstoneConfigs = tag.getList("RedstoneConfigs", Tag.TAG_COMPOUND);
        this.redstoneConfigs = new HashMap<>(1);
        for(int i = 0; i < redstoneConfigs.size(); i++) {
            CompoundTag entry = redstoneConfigs.getCompound(i);
            this.redstoneConfigs.put(entry.getInt("Index"), RedstoneType.valueOf(entry.getString("Type")));
        }
        this.redstoneConfigs.putIfAbsent(0, RedstoneType.IGNORED);
    }
    
    /**
     * Method used to save machine data when the machine is mined with a wrench.
     * Machine data is saved to an NBT tag which can later be restored when the machine is placed again.
     * This allows the machine's contents (e.g., items, fluids, energy) to be preserved during relocation.
     *
     * @param additionalTag The NBT tag used to store the machine contents
     */
    @Override
    public void saveContents(CompoundTag additionalTag) {
        super.saveContents(additionalTag);
        //additionalTag.put("SidedTransferConfigs", configuration.serializeNBT());
        CompoundTag tag = new CompoundTag();
        ListTag sideConfigs = new ListTag();
        this.sideConfigs.forEach((k, v) -> {
            if (v != TransferType.DEFAULT) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putString("Type", v.toString());
                sideConfigs.add(entry);
            }
        });
        tag.put("SideConfigs", sideConfigs);
        ListTag autoConfigs = new ListTag();
        this.autoConfigs.forEach((k, v) -> {
            if (v) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putBoolean("Enabled", v);
                autoConfigs.add(entry);
            }
        });
        tag.put("AutoConfigs", autoConfigs);
        ListTag redstoneConfigs = new ListTag();
        this.redstoneConfigs.forEach((k, v) -> {
            if (v != RedstoneType.IGNORED) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Index", k);
                entry.putString("Type", v.toString());
                redstoneConfigs.add(entry);
            }
        });
        tag.put("RedstoneConfigs", redstoneConfigs);
        additionalTag.put("SidedTransferConfigs", tag);
    }

    public static boolean canPerformTransfer(SidedTransferMachineBlockEntity<?> be, Direction dir, TransferType wanted) {
        List<Direction> available = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            SidedTransferMachineBlockEntity.TransferType type = be.sideConfigs.get(i) == null ? SidedTransferMachineBlockEntity.TransferType.DEFAULT : be.sideConfigs.get(i);
            if(type == wanted) available.add(SidedTransferMachineBlockEntity.resolveActualDir(be.getBlockState(), i));
        }
        return available.contains(dir);
    }

    public static boolean canPerformTransfers(SidedTransferMachineBlockEntity<?> be, Direction dir, TransferType... wanted) {
        for(TransferType type : wanted) {
            if(canPerformTransfer(be, dir, type)) return true;
        }
        return false;
    }

    public boolean areItemsAllowed() {
        return autoConfigs.get(2) == null || !autoConfigs.get(2);
    }

    public boolean areFluidsAllowed() {
        return autoConfigs.get(3) == null || !autoConfigs.get(3);
    }

    /*public static Direction resolveActualDir(BlockState state, int index) {
		if (state.getBlock() instanceof MachineFourWayBlock<?>) {
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
        else if (state.getBlock() instanceof MachineSixWayBlock<?>) {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            Direction.Axis axis;
            switch (facing) {
                case NORTH, SOUTH -> axis = Direction.Axis.Z;
				case EAST, WEST -> axis = Direction.Axis.X;
				default -> axis = Direction.Axis.Y;
            }
            return switch (index) {
                case 0 -> Direction.UP;
                case 1 -> facing.getClockWise(axis);
                case 2 -> facing;
                case 3 -> facing.getCounterClockWise(axis);
                case 4 -> Direction.DOWN;
                case 5 -> facing.getOpposite();
                default -> throw new IllegalStateException("Unexpected value: " + index);
            };
        }
        return Direction.NORTH;
    }*/
    
    public static Direction resolveActualDir(BlockState state, int index) {
        if (state.getBlock() instanceof MachineFourWayBlock<?>) {
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
        
        else if (state.getBlock() instanceof MachineSixWayBlock<?>) {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            
            return switch (index) {
                case 0 -> facing == Direction.UP ? Direction.SOUTH :
                    facing == Direction.DOWN ? Direction.NORTH : Direction.UP;
                case 1 -> switch (facing) {
                    case UP, DOWN, NORTH -> Direction.EAST;
					case SOUTH -> Direction.WEST;
                    case EAST -> Direction.SOUTH;
                    case WEST -> Direction.NORTH;
                };
                case 2 -> facing;
                case 3 -> switch (facing) {
                    case UP, DOWN, NORTH -> Direction.WEST;
					case SOUTH -> Direction.EAST;
                    case EAST -> Direction.NORTH;
                    case WEST -> Direction.SOUTH;
                };
                case 4 -> facing == Direction.UP ? Direction.NORTH :
                    facing == Direction.DOWN ? Direction.SOUTH : Direction.DOWN;
                case 5 -> facing.getOpposite();
                default -> throw new IllegalStateException("Unexpected value: " + index);
            };
        }
        return Direction.NORTH;
    }

    public void loadConfiguration(ItemStack drive) {
        if (drive.hasTag()){
            CompoundTag tag = drive.getTag();
            CompoundTag configs = tag.getCompound("SidedTransferConfigs");
            ListTag sideTag = configs.getList("SideConfigs", Tag.TAG_COMPOUND);
            this.sideConfigs = new HashMap<>(6);
            for (int i = 0; i < sideTag.size(); i++) {
                CompoundTag entry = sideTag.getCompound(i);
                this.sideConfigs.put(entry.getInt("Index"), TransferType.valueOf(entry.getString("Type")));
            }
            for (int i = 0; i < 6; i++) {
                this.sideConfigs.putIfAbsent(i, TransferType.DEFAULT);
            }
            this.autoConfigs = new HashMap<>(4);
            ListTag autoTag = configs.getList("AutoConfigs", Tag.TAG_COMPOUND);
            for (int i = 0; i < autoTag.size(); i++) {
                CompoundTag entry = autoTag.getCompound(i);
                this.autoConfigs.put(entry.getInt("Index"), entry.getBoolean("Enabled"));
            }
            for (int i = 0; i < 4; i++) {
                this.autoConfigs.putIfAbsent(i, false);
            }
            this.redstoneConfigs = new HashMap<>(1);
            ListTag redstoneTag = configs.getList("RedstoneConfigs", Tag.TAG_COMPOUND);
            for (int i = 0; i < redstoneTag.size(); i++) {
                CompoundTag entry = redstoneTag.getCompound(i);
                this.redstoneConfigs.put(entry.getInt("Index"), RedstoneType.valueOf(entry.getString("Type")));
            }
            this.redstoneConfigs.putIfAbsent(0, RedstoneType.IGNORED);
            sendUpdate();
        }
    }

    public enum TransferType {
        DEFAULT,
        NONE,
        PULL,
        PUSH;

        public static TransferType cycleNext(int index, SidedTransferMachineBlockEntity<?> entity) {
            TransferType type = entity.sideConfigs.get(index) == null ? DEFAULT : entity.sideConfigs.get(index);
            if(type == DEFAULT) return NONE;
            if(type == NONE) return PULL;
            if(type == PULL) return PUSH;
            return DEFAULT;
        }

        public static TransferType cyclePrev(int index, SidedTransferMachineBlockEntity<?> entity) {
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

        public static RedstoneType cycleNext(int index, SidedTransferMachineBlockEntity<?> entity) {
            RedstoneType type = entity.redstoneConfigs.get(index) == null ? IGNORED : entity.redstoneConfigs.get(index);
            if(type == IGNORED) return HIGH;
            if(type == HIGH) return LOW;
            return IGNORED;
        }

        public static RedstoneType cyclePrev(int index, SidedTransferMachineBlockEntity<?> entity) {
            RedstoneType type = entity.redstoneConfigs.get(index) == null ? IGNORED : entity.redstoneConfigs.get(index);
            if(type == IGNORED) return LOW;
            if(type == HIGH) return IGNORED;
            return HIGH;
        }
    }
}
