package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DimensionalAnchor extends GenericMachine {
	boolean markedForUpdate = false;
	public DimensionalAnchor(BlockPos pos, BlockState blockState) {
		super(ModMachines.DIMENSIONAL_ANCHOR, pos, blockState);
		setEnergy(() -> {
			int volume = getVolumeLevel();
			return new ModEnergyStorage(50000 * (volume + 1), 2500, 2500) {
				@Override
				public int receiveEnergyForced(int maxReceive, boolean simulate) {
					int received =  super.receiveEnergyForced(maxReceive, simulate);
					if (!simulate && received >= DimensionalAnchor.this.energyPerTick) markedForUpdate = true;
					return received;
				}
				
				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {
					int received = super.receiveEnergy(maxReceive, simulate);
					if (!simulate && received >= DimensionalAnchor.this.energyPerTick) markedForUpdate = true;
					return received;
				}
				
				@Override
				public void onEnergyChanged() {sendUpdate();}
				@Override
				public boolean canReceive() {return true;}
				@Override
				public boolean canExtract() {return false;}
			};
		});
		screenInfo.setShouldDrawArrow(false);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		if (!forcedChunks.isEmpty()) {
			setEnergyPerTick(forcedChunks.size() * 10);
			spendEnergy(energy);
			markedForUpdate = false;
		}
	}
	
	@Override
	public boolean needsUpdate() {
		return super.needsUpdate() || !canWork(energy) || markedForUpdate;
	}
	
	@Override
	public boolean canLoadChunks() {
		return canWork(energy);
	}
	
	@Override
	public int getLoadingRange() {
		return 3;
	}
}
