package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.content.block.entity.PictureScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PictureScreenBlock extends BaseEntityBlock {
	
	public static final IntegerProperty FACE_ROTATION = IntegerProperty.create("face_rotation", 0, 3);
	
	public PictureScreenBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(getStateDefinition().any()
			.setValue(BlockStateProperties.FACING, Direction.NORTH)
			.setValue(FACE_ROTATION, 0));
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
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof PictureScreen screen) {
			screen.setImageUrl("https://i.imgur.com/eE8fHHZ.jpeg");
			//screen.setImageUrl("https://i.imgur.com/KwpqFCi.jpeg");
			//screen.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/7/70/Example.png");
		}
		
		return super.use(state, level, pos, player, hand, hit);
	}
}
