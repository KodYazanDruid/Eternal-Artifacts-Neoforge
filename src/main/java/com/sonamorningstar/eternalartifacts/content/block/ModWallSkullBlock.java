package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.content.block.entity.ModSkullBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ModWallSkullBlock extends WallSkullBlock {
	public ModWallSkullBlock(SkullBlock.Type p_58101_, Properties p_58102_) {
		super(p_58101_, p_58102_);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ModSkullBlockEntity(pPos, pState);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pLevel.isClientSide) {
			boolean flag = pState.is(Blocks.DRAGON_HEAD)
				|| pState.is(Blocks.DRAGON_WALL_HEAD)
				|| pState.is(Blocks.PIGLIN_HEAD)
				|| pState.is(Blocks.PIGLIN_WALL_HEAD);
			if (flag) {
				return createTickerHelper(pBlockEntityType, ModBlockEntities.SKULL.get(), SkullBlockEntity::animation);
			}
		}
		
		return null;
	}
}
