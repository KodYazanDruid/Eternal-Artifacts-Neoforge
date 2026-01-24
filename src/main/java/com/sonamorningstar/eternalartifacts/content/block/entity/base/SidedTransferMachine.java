package com.sonamorningstar.eternalartifacts.content.block.entity.base;

import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.*;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineSixWayBlock;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.*;
import java.util.function.Predicate;

import static com.sonamorningstar.eternalartifacts.api.machine.config.SideConfig.TransferType;

@Setter
@Getter
public abstract class SidedTransferMachine<T extends AbstractMachineMenu> extends Machine<T> {
    public SidedTransferMachine(BlockEntityType<?> type, BlockPos pos, BlockState blockState, QuadFunction<Integer, Inventory, BlockEntity, ContainerData, T> quadF) {
        super(type, pos, blockState, quadF);
    }
    
    @Override
    public void registerConfigs() {
        super.registerConfigs();
        getConfiguration().add(new SideConfig());
        getConfiguration().add(new AutoTransferConfig());
    }

    //region Transfers
    protected void performAutoInputItems(Level lvl, BlockPos pos) {
        MachineConfiguration configuration = getConfiguration();
        SideConfig sideConfig = configuration.get(SideConfig.class);
        if (sideConfig == null) return;
        AutoTransferConfig autoTransferConfig = configuration.get(AutoTransferConfig.class);
        ReverseToggleConfig itemTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "item_transfer"));
        boolean isAllowedAuto = autoTransferConfig != null && autoTransferConfig.isInput();
        boolean isDisabled = itemTransferConfig != null && itemTransferConfig.isDisabled();
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            Direction dir = Direction.from3DDataValue(i);
            EnumMap<Direction, TransferType> sides = sideConfig.getSides();
            TransferType type = sides.get(dir) == null ? TransferType.DEFAULT : sides.get(dir);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            insertItemFromDir(lvl, pos, dir, inventory);
        }
    }
    
    protected void performAutoOutputItems(Level lvl, BlockPos pos) {
        performAutoOutputItems(lvl, pos, outputSlots, stack -> true);
    }
    protected void performAutoOutputItems(Level lvl, BlockPos pos, List<Integer> slots, Predicate<ItemStack> canOutput) {
        MachineConfiguration configuration = getConfiguration();
        SideConfig sideConfig = configuration.get(SideConfig.class);
        if (sideConfig == null) return;
        AutoTransferConfig autoTransferConfig = configuration.get(AutoTransferConfig.class);
        ReverseToggleConfig itemTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "item_transfer"));
        boolean isAllowedAuto = autoTransferConfig != null && autoTransferConfig.isOutput();
        boolean isDisabled = itemTransferConfig != null && itemTransferConfig.isDisabled();
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            Direction dir = Direction.from3DDataValue(i);
            EnumMap<Direction, TransferType> sides = sideConfig.getSides();
            TransferType type = sides.get(dir) == null ? TransferType.DEFAULT : sides.get(dir);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            outputItemToDir(lvl, pos, dir, inventory, slots, canOutput);
        }
    }

    protected void performAutoInputFluids(Level lvl, BlockPos pos) {
        MachineConfiguration configuration = getConfiguration();
        SideConfig sideConfig = configuration.get(SideConfig.class);
        if (sideConfig == null) return;
        AutoTransferConfig autoTransferConfig = configuration.get(AutoTransferConfig.class);
        ReverseToggleConfig fluidTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "fluid_transfer"));
        boolean isAllowedAuto = autoTransferConfig != null && autoTransferConfig.isInput();
        boolean isDisabled = fluidTransferConfig != null && fluidTransferConfig.isDisabled();
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            Direction dir = Direction.from3DDataValue(i);
            EnumMap<Direction, TransferType> sides = sideConfig.getSides();
            TransferType type = sides.get(dir) == null ? TransferType.DEFAULT : sides.get(dir);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            inputFluidFromDir(lvl, pos, dir, tank);
        }
    }

    protected void performAutoOutputFluids(Level lvl, BlockPos pos) {
        MachineConfiguration configuration = getConfiguration();
        SideConfig sideConfig = configuration.get(SideConfig.class);
        if (sideConfig == null) return;
        AutoTransferConfig autoTransferConfig = configuration.get(AutoTransferConfig.class);
        ReverseToggleConfig fluidTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "fluid_transfer"));
        boolean isAllowedAuto = autoTransferConfig != null && autoTransferConfig.isOutput();
        boolean isDisabled = fluidTransferConfig != null && fluidTransferConfig.isDisabled();
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            Direction dir = Direction.from3DDataValue(i);
            EnumMap<Direction, TransferType> sides = sideConfig.getSides();
            TransferType type = sides.get(dir) == null ? TransferType.DEFAULT : sides.get(dir);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            outputFluidToDir(lvl, pos, dir, tank);
        }
    }

    protected void performAutoInputEnergy(Level lvl, BlockPos pos) {
        MachineConfiguration configuration = getConfiguration();
        SideConfig sideConfig = configuration.get(SideConfig.class);
        if (sideConfig == null) return;
        AutoTransferConfig autoTransferConfig = configuration.get(AutoTransferConfig.class);
        ReverseToggleConfig energyTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "energy_transfer"));
        boolean isAllowedAuto = autoTransferConfig != null && autoTransferConfig.isInput();
        boolean isDisabled = energyTransferConfig != null && energyTransferConfig.isDisabled();
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> inputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            Direction dir = Direction.from3DDataValue(i);
            EnumMap<Direction, TransferType> sides = sideConfig.getSides();
            TransferType type = sides.get(dir) == null ? TransferType.DEFAULT : sides.get(dir);
            if(type == TransferType.PULL || type == TransferType.DEFAULT) inputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : inputDirs) {
            inputEnergyToDir(lvl, pos, dir, energy);
        }
    }

    protected void performAutoOutputEnergy(Level lvl, BlockPos pos) {
        MachineConfiguration configuration = getConfiguration();
        SideConfig sideConfig = configuration.get(SideConfig.class);
        if (sideConfig == null) return;
        AutoTransferConfig autoTransferConfig = configuration.get(AutoTransferConfig.class);
        ReverseToggleConfig energyTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "energy_transfer"));
        boolean isAllowedAuto = autoTransferConfig != null && autoTransferConfig.isOutput();
        boolean isDisabled = energyTransferConfig != null && energyTransferConfig.isDisabled();
        if(!isAllowedAuto || isDisabled) return;
        List<Direction> outputDirs = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            Direction dir = Direction.from3DDataValue(i);
            EnumMap<Direction, TransferType> sides = sideConfig.getSides();
            TransferType type = sides.get(dir) == null ? TransferType.DEFAULT : sides.get(dir);
            if(type == TransferType.PUSH || type == TransferType.DEFAULT) outputDirs.add(resolveActualDir(lvl.getBlockState(pos), i));
        }
        for(Direction dir : outputDirs) {
            outputEnergyToDir(lvl, pos, dir, energy);
        }
    }
    //endregion

    public static boolean canPerformTransfer(SidedTransferMachine<?> be, Direction dir, TransferType wanted) {
        MachineConfiguration configuration = be.getConfiguration();
        SideConfig sideConfig = configuration.get(SideConfig.class);
        if (sideConfig == null) return false;
        EnumMap<Direction, TransferType> sides = sideConfig.getSides();
        List<Direction> available = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            Direction d = Direction.from3DDataValue(i);
            TransferType type = sides.get(d) == null ? TransferType.DEFAULT : sides.get(d);
            if(type == wanted) available.add(SidedTransferMachine.resolveActualDir(be.getBlockState(), i));
        }
        return available.contains(dir);
    }

    public static boolean canPerformTransfers(SidedTransferMachine<?> be, Direction dir, TransferType... wanted) {
        for(TransferType type : wanted) {
            if(canPerformTransfer(be, dir, type)) return true;
        }
        return false;
    }

    public boolean areItemsAllowed() {
        MachineConfiguration configuration = getConfiguration();
        ReverseToggleConfig itemTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "item_transfer"));
        return itemTransferConfig == null || !itemTransferConfig.isDisabled();
    }

    public boolean areFluidsAllowed() {
        MachineConfiguration configuration = getConfiguration();
        ReverseToggleConfig fluidTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "fluid_transfer"));
        return fluidTransferConfig == null || !fluidTransferConfig.isDisabled();
    }
    
    public boolean isEnergyAllowed() {
        MachineConfiguration configuration = getConfiguration();
        ReverseToggleConfig energyTransferConfig = configuration.get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "energy_transfer"));
        return energyTransferConfig == null || !energyTransferConfig.isDisabled();
    }
    
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
}
