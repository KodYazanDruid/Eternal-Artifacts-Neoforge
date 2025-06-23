package com.sonamorningstar.eternalartifacts.content.block;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpongeBlock;

public class IndustrialSponge extends SpongeBlock {
	public IndustrialSponge(Properties p_56796_) {
		super(p_56796_);
	}
	
	@Override
	protected void tryAbsorbWater(Level pLevel, BlockPos pPos) {
		if (this.removeWaterBreadthFirstSearch(pLevel, pPos)) {
			pLevel.setBlock(pPos, ModBlocks.WET_INDUSTRIAL_SPONGE.get().defaultBlockState(), 2);
			pLevel.playSound(null, pPos, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}
}
