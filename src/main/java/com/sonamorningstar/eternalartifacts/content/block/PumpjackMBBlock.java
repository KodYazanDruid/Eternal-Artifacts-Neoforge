package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.base.AbstractMultiblockBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.PumpjackMB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PumpjackMBBlock extends AbstractMultiblockBlock {
	public PumpjackMBBlock(Properties props) {
		super(props);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new PumpjackMB(pPos, pState);
	}
}
