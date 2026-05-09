package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModMultiblocks;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.RelativeBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class PumpjackBlockEntity extends AbstractMultiblockBlockEntity {

	public PumpjackBlockEntity(BlockPos pos, BlockState state) {
		super(ModMultiblocks.PUMPJACK.getBlockEntity(), pos, state, ModMultiblocks.PUMPJACK.getMultiblock());
		setEnergy(() -> createBasicEnergy(200000, 2500, true, false));
		setTank(() -> new ModFluidStorage(50000, fs -> fs.is(ModTags.Fluids.CRUDE_OIL)));
		setEnergyPerTick(250);
	}
	
	@Override
	public boolean shouldSyncWorkingState() {
		return true;
	}
	
	@Override
	public boolean onDeform(Level level, BlockPos masterPos, RelativeBlockPos relativePos) {
		BlockPos deformedPos = masterPos.offset(relativePos.x(), relativePos.y(), relativePos.z());
		((ServerLevel) level).sendParticles(
			ParticleTypes.FLAME, deformedPos.getX() + 0.5, deformedPos.getY() + 1, deformedPos.getZ() + 0.5,
			5, 0, 0, 0, 0
		);
		return super.onDeform(level, masterPos, relativePos);
	}
	
	@Override
	public void tickMaster(Level lvl, BlockPos pos, BlockState st) {
		if (canWork(energy)) {
			FluidStack oil = ModFluids.CRUDE_OIL.getFluidStack(20);
			int inserted = tank.fillForced(oil, IFluidHandler.FluidAction.SIMULATE);
			if (inserted == oil.getAmount()) {
				setWorking(true);
				tank.fillForced(oil, IFluidHandler.FluidAction.EXECUTE);
				spendEnergy(energy);
			}
		} else setWorking(false);
	}
}
