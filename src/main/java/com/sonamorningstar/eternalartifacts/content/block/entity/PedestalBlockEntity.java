package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.DefaultRetexturedBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PedestalBlockEntity extends DefaultRetexturedBlockEntity {
	public ModItemStorage inventory;
	public PedestalBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntities.PEDESTAL.get(), pPos, pBlockState);
		this.texture = Blocks.COBBLESTONE;
		this.inventory = createBasicInventory(1, true);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Inventory", inventory.serializeNBT());
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		inventory.deserializeNBT(tag.getCompound("Inventory"));
	}
}
