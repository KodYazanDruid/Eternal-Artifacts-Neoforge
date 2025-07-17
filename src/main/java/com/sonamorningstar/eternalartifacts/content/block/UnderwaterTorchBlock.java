package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseTorchBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class UnderwaterTorchBlock extends BaseTorchBlock implements SimpleWaterloggedBlock {
	protected final SimpleParticleType particle;
	public UnderwaterTorchBlock(SimpleParticleType particle, Properties props) {
		super(props);
		this.particle = particle;
		registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
	}
	
	@Override
	protected MapCodec<? extends BaseTorchBlock> codec() {return null;}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		FluidState fluidstate = ctx.getLevel().getFluidState(ctx.getClickedPos());
		return defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
		return super.updateShape(state, dir, neighbor, levelAccessor, pos, neighborPos);
	}
	
	@Override
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
		double d0 = (double)pPos.getX() + 0.5;
		double d1 = (double)pPos.getY() + 0.7;
		double d2 = (double)pPos.getZ() + 0.5;
		pLevel.addParticle(this.particle, d0, d1, d2, 0.0, 0.0, 0.0);
	}
}
