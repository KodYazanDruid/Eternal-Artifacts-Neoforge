package com.sonamorningstar.eternalartifacts.caches;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidCombustionDynamoBlockEntity;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class DynamoProcessCache {
    @Getter
    private int duration;
    private final ModEnergyStorage energy;
    private final int generation;

    public DynamoProcessCache(int duration, ModEnergyStorage energy, int generation) {
        this.duration = duration;
        this.energy = energy;
        this.generation = generation;
    }

    public void process() {
        int inserted = energy.receiveEnergyForced(generation, true);
        if(inserted == generation) {
            duration--;
            energy.receiveEnergyForced(generation, false);
        }
    }

    public boolean isDone() {
        return duration <= 0;
    }

    public void writeToNbt(CompoundTag tag) {
        CompoundTag dynamoCache = new CompoundTag();
        dynamoCache.putInt("Duration", duration);
        dynamoCache.putInt("Generation", generation);
        tag.put("DynamoProcessCache", dynamoCache);
    }

    public static Optional<DynamoProcessCache> readFromNbt(CompoundTag tag, ModEnergyStorage energy, FluidCombustionDynamoBlockEntity dynamo) {
        CompoundTag dynamoCache = tag.getCompound("DynamoProcessCache");
        if(!dynamoCache.isEmpty()) {
            int duration = dynamoCache.getInt("Duration");
            int generation = dynamoCache.getInt("Generation");
            return Optional.of(new DynamoProcessCache(duration, energy, generation));
        }
        return Optional.empty();
    }
}
