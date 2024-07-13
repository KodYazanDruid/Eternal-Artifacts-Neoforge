package com.sonamorningstar.eternalartifacts.util.dynamo;

import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidCombustionDynamoBlockEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class DynamoProcessCache {
    @Getter
    private int duration;
    private final ModEnergyStorage energy;
    private final int generation;
    private final FluidCombustionDynamoBlockEntity dynamo;

    public DynamoProcessCache(int duration, ModEnergyStorage energy, int generation, FluidCombustionDynamoBlockEntity dynamo) {
        this.duration = duration;
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
        dynamoCache.putInt("Generation", generation);
        tag.put("DynamoProcessCache", dynamoCache);
    }

    public static Optional<DynamoProcessCache> readFromNbt(CompoundTag tag, ModEnergyStorage energy, FluidCombustionDynamoBlockEntity dynamo) {
        CompoundTag dynamoCache = tag.getCompound("DynamoProcessCache");
        if(!dynamoCache.isEmpty()) {
            int duration = dynamoCache.getInt("Duration");
            int generation = dynamoCache.getInt("Generation");
            return Optional.of(new DynamoProcessCache(duration, energy, generation, dynamo));
        }
        return Optional.empty();
    }
}
