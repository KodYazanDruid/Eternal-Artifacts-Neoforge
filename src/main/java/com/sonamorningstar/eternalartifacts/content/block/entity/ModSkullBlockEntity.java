package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModSkullBlockEntity extends SkullBlockEntity {
	public ModSkullBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(pPos, pBlockState);
	}
	
	@Override
	public BlockEntityType<?> getType() {
		return ModBlockEntities.SKULL.get();
	}
}
