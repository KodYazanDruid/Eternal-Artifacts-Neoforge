package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class PumpjackMB extends AbstractMultiblockBlockEntity {

	public PumpjackMB(BlockPos pos, BlockState state) {
		super(ModMultiblocks.PUMPJACK.getBlockEntity(), pos, state, ModMultiblocks.PUMPJACK.getMultiblock());
		setEnergy(() -> createBasicEnergy(200000, 2500, true, false));
		setTank(() -> new ModFluidStorage(50000, fs -> fs.is(ModTags.Fluids.CRUDE_OIL)));
		setEnergyPerTick(250);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		if (isMaster() && canWork(energy)) {
			FluidStack oil = ModFluids.CRUDE_OIL.getFluidStack(20);
			int inserted = tank.fillForced(oil, IFluidHandler.FluidAction.SIMULATE);
			if (inserted == oil.getAmount()) {
				tank.fillForced(oil, IFluidHandler.FluidAction.EXECUTE);
				spendEnergy(energy);
			}
		}
	}
}
