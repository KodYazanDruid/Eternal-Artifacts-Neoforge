package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackMB extends AbstractMultiblockBlockEntity {

	public PumpjackMB(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PUMPJACK.get(), pos, state, ModMultiblocks.PUMPJACK.get());
		setEnergy(() -> createBasicEnergy(200000, 2500, true, false));
		setTank(() -> new ModFluidStorage(50000, fs -> fs.is(ModTags.Fluids.CRUDE_OIL)));
	}
	
	
}
