package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.PictureScreen;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PictureScreenBlock extends BaseEntityBlock {
	
	public static final IntegerProperty FACE_ROTATION = IntegerProperty.create("face_rotation", 0, 3);
	
	public static final VoxelShape NORTH = BlockHelper.generateByArea(16, 16, 4, 0, 0, 12);
	public static final VoxelShape SOUTH = BlockHelper.generateByArea(16, 16, 4, 0, 0, 0);
	public static final VoxelShape EAST = BlockHelper.generateByArea(4, 16, 16, 0, 0, 0);
	public static final VoxelShape WEST = BlockHelper.generateByArea(4, 16, 16, 12, 0, 0);
	public static final VoxelShape UP = BlockHelper.generateByArea(16, 4, 16, 0, 0, 0);
	public static final VoxelShape DOWN = BlockHelper.generateByArea(16, 4, 16, 0, 12, 0);
	
	public PictureScreenBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(getStateDefinition().any()
			.setValue(BlockStateProperties.FACING, Direction.NORTH)
			.setValue(FACE_ROTATION, 0));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		switch (facing) {
			case NORTH -> {return NORTH;}
			case SOUTH -> {return SOUTH;}
			case EAST -> {return EAST;}
			case WEST -> {return WEST;}
			case UP -> {return UP;}
			case DOWN -> {return DOWN;}
		}
		return super.getShape(state, pLevel, pPos, pContext);
	}
	
	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {return simpleCodec(PictureScreenBlock::new);}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new PictureScreen(pPos, pState);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
		builder.add(FACE_ROTATION);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.is(ModTags.Items.TOOLS_WRENCH)) {
			level.setBlock(pos, state.cycle(FACE_ROTATION), 3);
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof PictureScreen screen) {
			player.openMenu(screen, pos);
			return InteractionResult.sidedSuccess(level.isClientSide);
			//screen.setImageUrl("https://i.imgur.com/eE8fHHZ.jpeg");
			//screen.setImageUrl("https://i.imgur.com/KwpqFCi.jpeg");
		}
		
		return super.use(state, level, pos, player, hand, hit);
	}
}
