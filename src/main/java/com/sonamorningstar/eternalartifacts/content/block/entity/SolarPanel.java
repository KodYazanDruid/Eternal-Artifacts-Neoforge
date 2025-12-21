package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.SolarPanelMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;

public class SolarPanel extends Machine<SolarPanelMenu> {
	public SolarPanel( BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SOLAR_PANEL.get(), pos, blockState, SolarPanelMenu::new);
		setEnergy(() -> {
			energyPerTick = getBlockState().getValue(SlabBlock.TYPE) == SlabType.DOUBLE ? 8 : 4;
			int cap = getBlockState().getValue(SlabBlock.TYPE) == SlabType.DOUBLE ? 32000 : 16000;
			return new SolarPanelEnergy(this, cap, 500);
		});
	}
	
	@Override
	public void setBlockState(BlockState pBlockState) {
		super.setBlockState(pBlockState);
		resetEnergy();
		this.invalidateCapabilities();
	}
	
	@Nullable
	public static ModEnergyStorage createEnergyCap(SolarPanel panel, @Nullable Direction dir) {
		SlabType type = panel.getBlockState().getValue(SlabBlock.TYPE);
		if (type == SlabType.DOUBLE) {
			if (dir != Direction.UP) {
				return panel.energy;
			}
		} else if (dir == Direction.DOWN || dir == null) {
			return panel.energy;
		}
		return null;
	}
	
	@Override
	public boolean isGenerator() {
		return true;
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		if (hasAnyEnergy(energy)) outputEnergyToDir(lvl, pos, Direction.DOWN, energy);
		int xO = pos.getX();
		int yO = pos.getY();
		int zO = pos.getZ();
		int y = lvl.getHeight(Heightmap.Types.WORLD_SURFACE, xO, zO);
		BlockPos checking = new BlockPos(xO, yO + 1, zO);
		boolean flag = true;
		while (checking.getY() <= y && flag) {
			BlockState state = lvl.getBlockState(checking);
			if (!(state.isAir() || state.propagatesSkylightDown(lvl, checking)) || state.is(ModBlocks.SOLAR_PANEL)) {
				flag = false;
			}
			checking = checking.offset(0, 1, 0);
		}
		if (lvl.isDay() && flag) {
			if (energy.getEnergyStored() < energy.getMaxEnergyStored()) {
				energy.receiveEnergyForced(energyPerTick, false);
			}
		}
	}
	
	public static class SolarPanelEnergy extends ModEnergyStorage {
		private final SolarPanel panel;
		
		public SolarPanelEnergy(SolarPanel panel, int capacity, int maxTransfer) {
			super(capacity, maxTransfer);
			this.panel = panel;
		}
		
		@Override
		public int extractEnergyForced(int maxExtract, boolean simulate) {return this.extractEnergy(maxExtract, simulate);}
		@Override
		public boolean canExtract() {return true;}
		@Override
		public boolean canReceive() {return false;}
		@Override
		public void onEnergyChanged() {panel.sendUpdate();}
	}
}
