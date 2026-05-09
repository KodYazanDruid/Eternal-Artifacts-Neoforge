package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GeneratorBlockEntity extends AbstractMultiblockBlockEntity {

	public GeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(ModMultiblocks.GENERATOR.getBlockEntity(), pos, state, ModMultiblocks.GENERATOR.getMultiblock());
		setEnergy(() -> createBasicEnergy(1_000_000, 10_000, false, true));
		setTank(() -> new ModFluidStorage(50_000));
		setInventory(() -> createBasicInventory(1, true));
	}
	
	@Override
	public boolean isGenerator() {
		return true;
	}
	
	@Override
	public void tickMaster(Level lvl, BlockPos pos, BlockState st) {
	
	}
}
