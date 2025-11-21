package com.sonamorningstar.eternalartifacts.content.block.base;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.TickableClient;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MultiblockBlock extends Block implements EntityBlock {
	private final BlockEntityType.BlockEntitySupplier<? extends AbstractMultiblockBlockEntity> fun;
	public MultiblockBlock(Properties props, BlockEntityType.BlockEntitySupplier<? extends AbstractMultiblockBlockEntity> fun) {
		super(props);
		this.fun = fun;
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level level, BlockPos pPos, Player pPlayer, InteractionHand hand, BlockHitResult pHit) {
		BlockEntity be = level.getBlockEntity(pPos);
		
		return super.use(pState, level, pPos, pPlayer, hand, pHit);
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState pNewState, boolean pMovedByPiston) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof AbstractMultiblockBlockEntity ambe) ambe.deformMultiblock();
		super.onRemove(state, level, pos, pNewState, pMovedByPiston);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return fun.create(pPos, pState);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (lvl, pos, st, be) -> {
			if (be instanceof AbstractMultiblockBlockEntity mbBe) {
				if (lvl.isClientSide() && mbBe instanceof TickableClient) ((TickableClient) mbBe).tickClient(lvl, pos, st);
				else mbBe.tickServer(lvl, pos, st);
			}
		};
	}
}
