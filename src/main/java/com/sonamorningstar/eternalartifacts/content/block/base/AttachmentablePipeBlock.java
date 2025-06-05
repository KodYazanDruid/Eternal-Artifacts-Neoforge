package com.sonamorningstar.eternalartifacts.content.block.base;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.FilterablePipeBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty;
import com.sonamorningstar.eternalartifacts.content.item.PipeExtractor;
import com.sonamorningstar.eternalartifacts.content.item.PipeFilter;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty.PipeConnection;

public abstract class AttachmentablePipeBlock<CAP> extends AbstractPipeBlock<CAP> {
	
	public static final PipeConnectionProperty NORTH_CONNECTION = PipeConnectionProperty.create("north_connection");
	public static final PipeConnectionProperty SOUTH_CONNECTION = PipeConnectionProperty.create("south_connection");
	public static final PipeConnectionProperty EAST_CONNECTION = PipeConnectionProperty.create("east_connection");
	public static final PipeConnectionProperty WEST_CONNECTION = PipeConnectionProperty.create("west_connection");
	public static final PipeConnectionProperty UP_CONNECTION = PipeConnectionProperty.create("up_connection");
	public static final PipeConnectionProperty DOWN_CONNECTION = PipeConnectionProperty.create("down_connection");
	
	public static final Map<Direction, PipeConnectionProperty> CONNECTION_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH_CONNECTION);
		map.put(Direction.EAST, EAST_CONNECTION);
		map.put(Direction.SOUTH, SOUTH_CONNECTION);
		map.put(Direction.WEST, WEST_CONNECTION);
		map.put(Direction.UP, UP_CONNECTION);
		map.put(Direction.DOWN, DOWN_CONNECTION);
	}));
	
	public AttachmentablePipeBlock(Class<CAP> cls, Properties props) {
		super(cls, props);
		registerDefaultState(getStateDefinition().any()
			.setValue(WATERLOGGED, false)
			.setValue(NORTH_CONNECTION, PipeConnection.NONE)
			.setValue(SOUTH_CONNECTION, PipeConnection.NONE)
			.setValue(EAST_CONNECTION, PipeConnection.NONE)
			.setValue(WEST_CONNECTION, PipeConnection.NONE)
			.setValue(UP_CONNECTION, PipeConnection.NONE)
			.setValue(DOWN_CONNECTION, PipeConnection.NONE));
	}
	
	@Override
	protected boolean shouldRegisterDefaultState() {
		return false;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(
			WATERLOGGED,
			NORTH_CONNECTION,
			SOUTH_CONNECTION,
			EAST_CONNECTION,
			WEST_CONNECTION,
			UP_CONNECTION,
			DOWN_CONNECTION
		);
	}
	
	@Override
	public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState state, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
		super.playerDestroy(pLevel, pPlayer, pPos, state, pBlockEntity, pTool);
		for (Direction dir : Direction.values()) {
			PipeConnection connection = state.getValue(CONNECTION_BY_DIRECTION.get(dir));
			if (connection == PipeConnection.EXTRACT) {
				Block.popResource(pLevel, pPos, ModItems.PIPE_EXTRACTOR.toStack());
			} else if (connection == PipeConnection.FILTERED) {
				Block.popResource(pLevel, pPos, ModItems.PIPE_FILTER.toStack());
			}
		}
	}
	
	protected abstract boolean checkPipe(BlockState relativeState);
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof AbstractPipeBlockEntity<?> pipe)) return InteractionResult.PASS;
		
		Direction relativeDir = getClickedRelativePos(hit.getDirection(), pos, hit.getLocation(), 8);
		BlockState relativeState = level.getBlockState(pos.relative(relativeDir));
		
		if (checkPipe(relativeState)) {
			return InteractionResult.PASS;
		}
		
		ItemStack stack = player.getItemInHand(hand);
		PipeConnection connection = state.getValue(CONNECTION_BY_DIRECTION.get(relativeDir));
		
		boolean isCreative = player.getAbilities().instabuild;
		
		if (player.isShiftKeyDown() && stack.isEmpty()) {
			if (connection == PipeConnection.EXTRACT) {
				level.setBlockAndUpdate(pos, state.setValue(CONNECTION_BY_DIRECTION.get(relativeDir), PipeConnection.NONE));
				ItemStack extractorStack = ModItems.PIPE_EXTRACTOR.toStack();
				if (pipe instanceof FilterablePipeBlockEntity<?> filterablePipe) {
					CompoundTag data = filterablePipe.saveAndRemoveForDir(relativeDir);
					extractorStack.setTag(data);
				}
				if (!isCreative) Block.popResourceFromFace(level, pos, hit.getDirection(), extractorStack);
				pipe.updateConnections(level);
				return InteractionResult.sidedSuccess(level.isClientSide());
			} else if (connection == PipeConnection.FILTERED) {
				level.setBlockAndUpdate(pos, state.setValue(CONNECTION_BY_DIRECTION.get(relativeDir), PipeConnection.NONE));
				ItemStack filterStack = ModItems.PIPE_FILTER.toStack();
				if (pipe instanceof FilterablePipeBlockEntity<?> filterablePipe) {
					CompoundTag data = filterablePipe.saveAndRemoveForDir(relativeDir);
					filterStack.setTag(data);
				}
				if (!isCreative) Block.popResourceFromFace(level, pos, hit.getDirection(), filterStack);
				pipe.updateConnections(level);
				return InteractionResult.sidedSuccess(level.isClientSide());
			} else if (connection == PipeConnection.FREE) {
				level.setBlockAndUpdate(pos, state.setValue(CONNECTION_BY_DIRECTION.get(relativeDir), PipeConnection.NONE));
				pipe.updateConnections(level);
				return InteractionResult.sidedSuccess(level.isClientSide());
			} else if (connection == PipeConnection.NONE) {
				level.setBlockAndUpdate(pos, state.setValue(CONNECTION_BY_DIRECTION.get(relativeDir), PipeConnection.FREE));
				pipe.updateConnections(level);
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		} else {
			if (stack.getItem() instanceof PipeExtractor) {
				if (connection == PipeConnection.NONE || connection == PipeConnection.FREE) {
					level.setBlockAndUpdate(pos, state.setValue(CONNECTION_BY_DIRECTION.get(relativeDir), PipeConnection.EXTRACT));
					if (pipe instanceof FilterablePipeBlockEntity<?> filterablePipe) {
						filterablePipe.loadFromItemFilter(stack, relativeDir);
					}
					if (!isCreative) stack.shrink(1);
					pipe.updateConnections(level);
					return InteractionResult.sidedSuccess(level.isClientSide());
				}
			} else if (stack.getItem() instanceof PipeFilter) {
				if (connection == PipeConnection.NONE || connection == PipeConnection.FREE) {
					level.setBlockAndUpdate(pos, state.setValue(CONNECTION_BY_DIRECTION.get(relativeDir), PipeConnection.FILTERED));
					if (pipe instanceof FilterablePipeBlockEntity<?> filterablePipe) {
						filterablePipe.loadFromItemFilter(stack, relativeDir);
					}
					if (!isCreative) stack.shrink(1);
					pipe.updateConnections(level);
					return InteractionResult.sidedSuccess(level.isClientSide());
				}
			} else if (stack.isEmpty() && player instanceof ServerPlayer sp &&
				(connection == PipeConnection.EXTRACT || connection == PipeConnection.FILTERED)) {
				pipe.openMenu(sp, relativeDir);
				return InteractionResult.CONSUME;
			}
		}
		return super.use(state, level, pos, player, hand, hit);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult hit, LevelReader level, BlockPos pos, Player player) {
		if (!(hit instanceof BlockHitResult hitResult)) return super.getCloneItemStack(state, hit, level, pos, player);
		Direction relativeDir = getClickedRelativePos(hitResult.getDirection(), pos, hit.getLocation(), 8);
		PipeConnectionProperty.PipeConnection connection = state.getValue(CONNECTION_BY_DIRECTION.get(relativeDir));
		if (connection == PipeConnectionProperty.PipeConnection.EXTRACT) {
			return ModItems.PIPE_EXTRACTOR.toStack();
		} else if (connection == PipeConnectionProperty.PipeConnection.FILTERED) {
			return ModItems.PIPE_FILTER.toStack();
		}
		return super.getCloneItemStack(state, hitResult, level, pos, player);
	}
}
