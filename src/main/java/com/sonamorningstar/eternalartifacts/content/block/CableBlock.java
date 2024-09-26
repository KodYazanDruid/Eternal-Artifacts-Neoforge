package com.sonamorningstar.eternalartifacts.content.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CableBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), map -> {
        map.put(Direction.NORTH, NORTH);
        map.put(Direction.EAST, EAST);
        map.put(Direction.SOUTH, SOUTH);
        map.put(Direction.WEST, WEST);
        map.put(Direction.UP, UP);
        map.put(Direction.DOWN, DOWN);
    }));
    private static final VoxelShape SHAPE = BlockHelper.generateByArea(6, 6, 6, 5, 5, 5);
    private static final VoxelShape SHAPE_NORTH = BlockHelper.generateByArea(6, 6, 5, 5, 5, 0);
    private static final VoxelShape SHAPE_SOUTH = BlockHelper.generateByArea(6, 6, 5, 5, 5, 11);
    private static final VoxelShape SHAPE_EAST = BlockHelper.generateByArea(5, 6, 6, 11, 5, 5);
    private static final VoxelShape SHAPE_WEST = BlockHelper.generateByArea(5, 6, 6, 0, 5, 5);
    private static final VoxelShape SHAPE_UP = BlockHelper.generateByArea(6, 5, 6, 5, 11, 5);
    private static final VoxelShape SHAPE_DOWN = BlockHelper.generateByArea(6, 5, 6, 5, 0, 5);

    public CableBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState()
            .setValue(WATERLOGGED, false)
            .setValue(NORTH, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(EAST, false)
            .setValue(UP, false)
            .setValue(DOWN, false)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape joinedShape = SHAPE;
        boolean isNorth = state.getValue(NORTH);
        boolean isEast = state.getValue(EAST);
        boolean isSouth = state.getValue(SOUTH);
        boolean isWest = state.getValue(WEST);
        boolean isUp = state.getValue(UP);
        boolean isDown = state.getValue(DOWN);

        if (isNorth) joinedShape = Shapes.or(joinedShape, SHAPE_NORTH);
        if (isEast) joinedShape = Shapes.or(joinedShape, SHAPE_EAST);
        if (isSouth) joinedShape = Shapes.or(joinedShape, SHAPE_SOUTH);
        if (isWest) joinedShape = Shapes.or(joinedShape, SHAPE_WEST);
        if (isUp) joinedShape = Shapes.or(joinedShape, SHAPE_UP);
        if (isDown) joinedShape = Shapes.or(joinedShape, SHAPE_DOWN);

        return joinedShape;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {return RenderShape.MODEL;}
    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {return false;}
    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) { return false; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    public boolean connectsTo(BlockPos pos, Level level, Direction direction) {
        BlockState state = level.getBlockState(pos);
        IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction);
        return energyStorage != null || state.is(this);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);

        BlockPos north = pos.north();
        BlockPos east = pos.east();
        BlockPos south = pos.south();
        BlockPos west = pos.west();
        BlockPos up = pos.above();
        BlockPos down = pos.below();

        return defaultBlockState()
                .setValue(NORTH, connectsTo(north, level, Direction.NORTH.getOpposite()))
                .setValue(EAST, connectsTo(east, level, Direction.EAST.getOpposite()))
                .setValue(SOUTH, connectsTo(south, level, Direction.SOUTH.getOpposite()))
                .setValue(WEST, connectsTo(west, level, Direction.WEST.getOpposite()))
                .setValue(UP, connectsTo(up, level, Direction.UP.getOpposite()))
                .setValue(DOWN, connectsTo(down, level, Direction.DOWN.getOpposite()))
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighState, LevelAccessor accessor, BlockPos pos, BlockPos neighPos) {
        if(state.getValue(WATERLOGGED)) accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
        boolean canConnect = accessor instanceof Level level && connectsTo(neighPos, level, direction.getOpposite());
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), canConnect);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}