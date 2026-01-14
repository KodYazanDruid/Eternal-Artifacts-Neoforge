package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.NaphthaCauldronBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.TickableServer;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CrudeOilCauldron extends ModBlockEntity implements TickableServer {
	private final int cooldownValue = 300;
	public int cooldown = cooldownValue;
	
	public CrudeOilCauldron(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CRUDE_OIL_CAULDRON.get(), pos, state);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("Cooldown", cooldown);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		cooldown = tag.getInt("Cooldown");
	}
	
	@Override
	protected boolean shouldSyncOnUpdate() { return true; }
	
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		if (!hasHeatSource(lvl, pos)) {
			resetCooldown();
			sendUpdate();
			return;
		}
		if (cooldown > 0) {
			cooldown--;
		} else {
			cooldown = cooldownValue;
			lvl.setBlockAndUpdate(pos, ModBlocks.NAPHTHA_CAULDRON.get().defaultBlockState()
				.setValue(NaphthaCauldronBlock.LEVEL, NaphthaCauldronBlock.MIN_LEVEL));
			lvl.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}
	
	public void resetCooldown() {
		cooldown = cooldownValue;
	}
	
	public boolean hasHeatSource(Level lvl, BlockPos pos) {
		BlockState belowState = lvl.getBlockState(pos.below());
		boolean isHeated = belowState.is(Blocks.BLAST_FURNACE) && belowState.getValue(BlockStateProperties.LIT);
		if (!isHeated) isHeated = belowState.is(Blocks.MAGMA_BLOCK);
		return isHeated;
	}
}
