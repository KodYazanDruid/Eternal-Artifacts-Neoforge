package com.sonamorningstar.eternalartifacts.api.caches;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import net.minecraft.nbt.CompoundTag;

public class InfiniteDynamoProcessCache extends DynamoProcessCache {
	public InfiniteDynamoProcessCache(ModEnergyStorage energy, int generation, AbstractDynamo<?> dynamo) {
		super(Integer.MAX_VALUE, Integer.MAX_VALUE, energy, generation, dynamo);
	}

	@Override
	public void process() {
		int inserted = getEnergy().receiveEnergyForced(getGeneration(), true);
		if(inserted == getGeneration() && canContinueUse()) {
			getEnergy().receiveEnergyForced(getGeneration(), false);
			getDynamo().setWorking(true);
			getOnProcessListeners().forEach(con -> con.accept(this));
		} else {
			getDynamo().setWorking(false);
		}
	}

	@Override
	public boolean isDone() {
		return false;
	}
	
	public boolean canContinueUse() {
		return true;
	}
	
	@Override
	public void writeToNbt(CompoundTag tag) {
		CompoundTag dynamoCache = new CompoundTag();
		dynamoCache.putString("Type", "Infinite");
		dynamoCache.putInt("Generation", getGeneration());
		tag.put("DynamoProcessCache", dynamoCache);
	}
}
