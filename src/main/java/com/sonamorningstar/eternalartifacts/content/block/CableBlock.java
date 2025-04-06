package com.sonamorningstar.eternalartifacts.content.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sonamorningstar.eternalartifacts.content.block.entity.CableBlockEntity;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import net.neoforged.neoforge.common.IExtensibleEnum;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
@SuppressWarnings({"deprecation"})
public class CableBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    private final CableTier tier;
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

    public CableBlock(CableTier tier, Properties props) {
        super(props);
        this.tier = tier;
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);
        
        return defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighState, LevelAccessor accessor, BlockPos pos, BlockPos neighPos) {
        if(state.getValue(WATERLOGGED)) accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
        if (accessor instanceof Level level && level.getBlockEntity(pos) instanceof CableBlockEntity cable) {
            cable.updateConnections(level);
        }
        return state;
    }
    
    @Override
    public void onNeighborChange(BlockState state, LevelReader reader, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, reader, pos, neighbor);
        if (reader instanceof Level level && reader.getBlockEntity(pos) instanceof CableBlockEntity cable) {
            cable.updateConnections(level);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new CableBlockEntity(pos, state);}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : (lvl, pos, state, be) -> {
            if (be instanceof CableBlockEntity cable) cable.tickServer(lvl, pos, state);
        };
    }
    
    @Getter
    public enum CableTier implements IExtensibleEnum {
        COPPER(16, 1000),
        GOLD(32, 4000);
        
        private final int maxConnections;
        private final int transferRate;
        
        CableTier(int maxConnections, int transferRate) {
            this.maxConnections = maxConnections;
            this.transferRate = transferRate;
        }
        
        public static CableTier create(String name, int maxConnections, int transferRate) {
            throw new IllegalStateException("Enum not extended");
        }
    }
}
