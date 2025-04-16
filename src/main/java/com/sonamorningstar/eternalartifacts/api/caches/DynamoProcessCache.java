package com.sonamorningstar.eternalartifacts.api.caches;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
@Getter
public class DynamoProcessCache {
    private int duration;
    private final int maxDuration;
    private final ModEnergyStorage energy;
    private final int generation;
    private final AbstractDynamo<?> dynamo;
    
    public DynamoProcessCache(int duration, int maxDuration, ModEnergyStorage energy, int generation, AbstractDynamo<?> dynamo) {
        this.duration = duration;
        this.maxDuration = maxDuration;
        this.energy = energy;
        this.generation = generation;
        this.dynamo = dynamo;
    }

    public void process() {
        int inserted = energy.receiveEnergyForced(generation, true);
        if(inserted == generation) {
            duration--;
            energy.receiveEnergyForced(generation, false);
            dynamo.isWorking = true;
            dynamo.sendUpdate();
        } else {
            dynamo.isWorking = false;
            dynamo.sendUpdate();
        }

    }

    public boolean isDone() {
        return duration <= 0;
    }

    public void writeToNbt(CompoundTag tag) {
        CompoundTag dynamoCache = new CompoundTag();
        dynamoCache.putInt("Duration", duration);
        dynamoCache.putInt("MaxDuration", maxDuration);
        dynamoCache.putInt("Generation", generation);
        tag.put("DynamoProcessCache", dynamoCache);
    }

    public static Optional<DynamoProcessCache> readFromNbt(CompoundTag tag, ModEnergyStorage energy, AbstractDynamo<?> dynamo) {
        CompoundTag dynamoCache = tag.getCompound("DynamoProcessCache");
        if(!dynamoCache.isEmpty()) {
            int duration = dynamoCache.getInt("Duration");
            int maxDuration = dynamoCache.getInt("MaxDuration");
            int generation = dynamoCache.getInt("Generation");
            return Optional.of(new DynamoProcessCache(duration, maxDuration, energy, generation, dynamo));
        }
        return Optional.empty();
    }
}
