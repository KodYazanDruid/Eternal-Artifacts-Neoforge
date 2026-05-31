package com.sonamorningstar.eternalartifacts.api.caches;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class DynamoProcessCache {
    private int duration;
    private final int maxDuration;
    private final ModEnergyStorage energy;
    private final int generation;
    private final AbstractDynamo<?> dynamo;
    private final List<Consumer<DynamoProcessCache>> onProcessListeners = new ArrayList<>();
    
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
            dynamo.setWorking(true);
            onProcessListeners.forEach(con -> con.accept(this));
        } else {
            dynamo.setWorking(false);
        }

    }

    public boolean isDone() {
        return duration <= 0;
    }
    
    public void addOnProcessListener(Consumer<DynamoProcessCache> listener) {
        onProcessListeners.add(listener);
    }
    
    public void writeToNbt(CompoundTag tag) {
        CompoundTag dynamoCache = new CompoundTag();
        dynamoCache.putString("Type", "Normal");
        dynamoCache.putInt("Duration", duration);
        dynamoCache.putInt("MaxDuration", maxDuration);
        dynamoCache.putInt("Generation", generation);
        tag.put("DynamoProcessCache", dynamoCache);
    }

    public static Optional<DynamoProcessCache> readFromNbt(CompoundTag tag, ModEnergyStorage energy, AbstractDynamo<?> dynamo) {
        CompoundTag dynamoCache = tag.getCompound("DynamoProcessCache");
        if(!dynamoCache.isEmpty()) {
            String type = dynamoCache.getString("Type");
            int generation = dynamoCache.getInt("Generation");
            if ("Normal".equals(type)){
                int duration = dynamoCache.getInt("Duration");
                int maxDuration = dynamoCache.getInt("MaxDuration");
                return Optional.of(new DynamoProcessCache(duration, maxDuration, energy, generation, dynamo));
            } else if ("Infinite".equals(type)) {
                return Optional.of(new InfiniteDynamoProcessCache(energy, generation, dynamo));
            }
        }
        return Optional.empty();
    }
}
