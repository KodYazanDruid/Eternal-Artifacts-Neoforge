package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GeneratorMB extends AbstractMultiblockBlockEntity {

	public GeneratorMB(BlockPos pos, BlockState state) {
		super(ModMultiblocks.GENERATOR.getBlockEntity(), pos, state, ModMultiblocks.GENERATOR.getMultiblock());
		setEnergy(() -> createBasicEnergy(100000, 1000, true, false));
		setTank(() -> new ModFluidStorage(10000));
		setInventory(() -> createBasicInventory(1, true));
	}
}
