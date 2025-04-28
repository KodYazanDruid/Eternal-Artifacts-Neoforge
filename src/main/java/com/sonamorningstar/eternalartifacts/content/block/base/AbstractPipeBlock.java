package com.sonamorningstar.eternalartifacts.content.block.base;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractPipeBlock<CAP> extends Block implements EntityBlock, SimpleWaterloggedBlock {
	private final Class<CAP> capabilityClass;
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
	
	public AbstractPipeBlock(Class<CAP> cls, BlockBehaviour.Properties props) {
		super(props);
		this.capabilityClass = cls;
		if (shouldRegisterDefaultState()) {
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
	}
	
	protected boolean shouldRegisterDefaultState() {
		return true;
	}
	
	public Direction getClickedRelativePos(Direction dir, BlockPos pos, Vec3 clickedPoint, int edgeLength) {
		Vec3 relativePos = clickedPoint.subtract(pos.getX(), pos.getY(), pos.getZ());
		int offset = 16 - edgeLength;
		int min = offset / 2;
		int max = 16 - min;
		return switch (dir) {
			case UP, DOWN -> {
				if (relativePos.x > (double) max / 16) yield Direction.EAST;
				else if (relativePos.x < (double) min / 16) yield Direction.WEST;
				else {
					if (relativePos.z > (double) max / 16) yield Direction.SOUTH;
					else if (relativePos.z < (double) min / 16)yield Direction.NORTH;
					else {
						if (relativePos.y > 0.5) yield Direction.UP;
						else yield Direction.DOWN;
					}
				}
			}
			case EAST, WEST -> {
				if (relativePos.y > (double) max / 16) yield Direction.UP;
				else if (relativePos.y < (double) min / 16) yield Direction.DOWN;
				else {
					if (relativePos.z > (double) max / 16) yield Direction.SOUTH;
					else if (relativePos.z < (double) min / 16) yield Direction.NORTH;
					else {
						if (relativePos.x > 0.5) yield Direction.EAST;
						else yield Direction.WEST;
					}
				}
			}
			case NORTH, SOUTH -> {
				if (relativePos.y > (double) max / 16) yield Direction.UP;
				else if (relativePos.y < (double) min / 16) yield Direction.DOWN;
				else {
					if (relativePos.x > (double) max / 16) yield Direction.EAST;
					else if (relativePos.x < (double) min / 16) yield Direction.WEST;
					else {
						if (relativePos.z > 0.5) yield Direction.SOUTH;
						else yield Direction.NORTH;
					}
				}
			}
		};
	}
	
	@Override
	public boolean hasDynamicShape() {
		return true;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {return RenderShape.MODEL;}
	@Override
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) { return false; }
	
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
		if (accessor instanceof Level level && level.getBlockEntity(pos) instanceof AbstractPipeBlockEntity<?> pipe &&
			Objects.equals(capabilityClass, pipe.getCapabilityClass())) {
			pipe.updateConnections(level);
		}
		return state;
	}
	
	@Override
	public void onNeighborChange(BlockState state, LevelReader reader, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, reader, pos, neighbor);
		if (reader instanceof Level level && reader.getBlockEntity(pos) instanceof AbstractPipeBlockEntity<?> pipe &&
			Objects.equals(capabilityClass, pipe.getCapabilityClass())) {
			pipe.updateConnections(level);
		}
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide() ? null : (lvl, pos, state, be) -> {
			if (be instanceof AbstractPipeBlockEntity<?> pipe) pipe.tickServer(lvl, pos, state);
		};
	}
}
