package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.config.ConfigLocations;
import com.sonamorningstar.eternalartifacts.api.machine.config.ReverseToggleConfig;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.WorkingAreaProvider;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.EnergyDistributorTargetsToClient;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyDistributor extends GenericMachine implements WorkingAreaProvider {
	public LongList targets;
	public long[] workingPositions;
	public EnergyDistributor(BlockPos pos, BlockState blockState) {
		super(ModMachines.ENERGY_DISTRIBUTOR, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		registerCapabilityConfigs(Capabilities.EnergyStorage.BLOCK);
		screenInfo.setShowEPT(false);
		screenInfo.setShouldDrawArrow(false);
		screenInfo.setShouldDrawInventoryTitle(false);
	}
	
	@Override
	public void registerCapabilityConfigs(BlockCapability<?, ?> cap) {
		super.registerCapabilityConfigs(cap);
		if (cap == Capabilities.EnergyStorage.BLOCK) getConfiguration().add(new ReverseToggleConfig("energy_transfer"));
	}
	
	@Override
	public AABB getWorkingArea(BlockPos anchor) {
		return new AABB(anchor).inflate(5);
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		workingPositions = BlockPos.MutableBlockPos.betweenClosedStream(getWorkingArea(getBlockPos()).contract(1,1,1)).mapToLong(BlockPos::asLong).toArray();
		targets = new LongArrayList();
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		
		boolean updateTargets = targets != null && !targets.isEmpty();
		targets = new LongArrayList();
		for (long workingPosition : workingPositions) {
			BlockPos targetPos = BlockPos.of(workingPosition);
			if (targetPos.equals(pos)) continue;
			boolean foundTarget = false;
			for (Direction value : Direction.values()) {
				IEnergyStorage targetEnergy = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, value);
				long targetLong = targetPos.asLong();
				if (!foundTarget && !targets.contains(targetLong) && targetEnergy != null && targetEnergy.canReceive()) {
					targets.add(targetLong);
					updateTargets = true;
					foundTarget = true;
				}
			}
		}
		
		if (updateTargets) {
			Channel.sendToChunk(new EnergyDistributorTargetsToClient(pos, targets), lvl.getChunkAt(pos));
		}
		
		ReverseToggleConfig energyTransferConfig = getConfiguration().get(ConfigLocations.getWithSuffix(ReverseToggleConfig.class, "energy_transfer"));
		if (energyTransferConfig != null && energyTransferConfig.isDisabled()) return;
		
		for (long target : targets) {
			BlockPos targetPos = BlockPos.of(target);
			int toTransfer = energyTransferRate;
			for (Direction value : Direction.values()) {
				IEnergyStorage targetEnergy = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, value);
				if (targetEnergy != null && toTransfer > 0) {
					int received = targetEnergy.receiveEnergy(toTransfer, true);
					if (received > 0) {
						int extracted = energy.extractEnergyForced(received, true);
						if (extracted > 0) {
							int actuallyReceived = targetEnergy.receiveEnergy(extracted, false);
							energy.extractEnergyForced(actuallyReceived, false);
							toTransfer -= actuallyReceived;
						}
					}
				}
			}
		}
	}
}
