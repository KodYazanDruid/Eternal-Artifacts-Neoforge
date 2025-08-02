package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableClient;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMultiblockBlock extends Block implements EntityBlock {
	public AbstractMultiblockBlock(Properties props) {
		super(props);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level level, BlockPos pPos, Player pPlayer, InteractionHand hand, BlockHitResult pHit) {
		BlockEntity be = level.getBlockEntity(pPos);
		if (be instanceof AbstractMultiblockBlockEntity mbBe && !level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
			System.out.println("Using multiblock at " + mbBe.getFrontLeftPos());
		}
		return super.use(pState, level, pPos, pPlayer, hand, pHit);
	}
	
	@Override
	public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
		if (!(pLevel instanceof Level level)) return;
		BlockEntity be = level.getBlockEntity(pPos);
		if (be instanceof AbstractMultiblockBlockEntity mbBe) {
		
		}
		super.destroy(pLevel, pPos, pState);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (lvl, pos, st, be) -> {
			if (be instanceof AbstractMultiblockBlockEntity mbBe) {
				if (lvl.isClientSide() && mbBe instanceof ITickableClient) ((ITickableClient) mbBe).tickClient(lvl, pos, st);
				else mbBe.tickServer(lvl, pos, st);
			}
		};
	}
}
