package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class BeaconAgitator extends ModBlockEntity {
	@Nullable
	public UUID ownerUUID;
	public static final String UUID_KEY = "OwnerUUID";
	
	public BeaconAgitator(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BEACON_AGITATOR.get(), pos, state);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (ownerUUID != null) {
			tag.putUUID(UUID_KEY, ownerUUID);
		}
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.hasUUID(UUID_KEY)) {
			ownerUUID = tag.getUUID(UUID_KEY);
		}
	}
}
