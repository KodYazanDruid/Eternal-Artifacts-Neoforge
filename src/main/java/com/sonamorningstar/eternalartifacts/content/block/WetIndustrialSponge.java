package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WetSpongeBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WetIndustrialSponge extends WetSpongeBlock {
	public WetIndustrialSponge(Properties p_58222_) {
		super(p_58222_);
	}
	
	@Override
	public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
		if (pLevel.dimensionType().ultraWarm()) {
			pLevel.setBlock(pPos, ModBlocks.INDUSTRIAL_SPONGE.get().defaultBlockState(), 3);
			pLevel.levelEvent(2009, pPos, 0);
			pLevel.playSound(null, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, (1.0F + pLevel.getRandom().nextFloat() * 0.2F) * 0.7F);
		}
	}
}
