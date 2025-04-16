package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.SolarPanelMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;

public class SolarPanel extends MachineBlockEntity<SolarPanelMenu> {
	public SolarPanel( BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SOLAR_PANEL.get(), pos, blockState, SolarPanelMenu::new);
		setEnergy(() -> {
			int vol = getEnchantmentLevel(ModEnchantments.VOLUME.get());
			return new SolarPanelEnergy(this, 16000 * (vol + 1), 500);
		});
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
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
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
				energy.receiveEnergyForced(st.getValue(SlabBlock.TYPE) == SlabType.DOUBLE ? 8 : 4, false);
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
		public int getMaxEnergyStored() {
			SlabType type = panel.getBlockState().getValue(SlabBlock.TYPE);
			int max = super.getMaxEnergyStored();
			return type == SlabType.DOUBLE ? 2 * max : max;
		}
		
		@Override
		public boolean canExtract() {return true;}
		@Override
		public boolean canReceive() {return false;}
		@Override
		public void onEnergyChanged() {panel.sendUpdate();}
	}
}
