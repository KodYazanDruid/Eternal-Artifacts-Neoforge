package com.sonamorningstar.eternalartifacts.capabilities.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.EnergyStorage;

public abstract class ModEnergyStorage extends EnergyStorage {
    private static final int ENERGY_AVG_WINDOW = 20; // 20–40
    
    private int receivedThisTick = 0;
    private int extractedThisTick = 0;
    private int receivedLastTick = 0;
    private int extractedLastTick = 0;
    private final int[] receivedHistory = new int[ENERGY_AVG_WINDOW];
    private final int[] extractedHistory = new int[ENERGY_AVG_WINDOW];
    private int historyIndex = 0;
    private int historyCount = 0;
    public ModEnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract){
        super(capacity, maxReceive, maxExtract);
    }
	
	public int getMaxReceive() {
        return maxReceive;
    }
    public int getMaxExtract() {
        return maxExtract;
    }
    
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received != 0) {
            receivedThisTick += received;
            onEnergyChanged();
        }
        return received;
    }
    
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && extracted != 0) {
            extractedThisTick += extracted;
            onEnergyChanged();
        }
        return extracted;
    }
    
    public int receiveEnergyForced(int maxReceive, boolean simulate) {
        int received = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate && received != 0) {
            energy += received;
            receivedThisTick += received;
            onEnergyChanged();
        }
        return received;
    }
    
    public int extractEnergyForced(int maxExtract, boolean simulate) {
        int extracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate && extracted != 0) {
            energy -= extracted;
            extractedThisTick += extracted;
            onEnergyChanged();
        }
        return extracted;
    }
    
    public int getAverageReceivedEnergy() {
        if (historyCount == 0) return 0;
        int sum = 0;
        for (int i = 0; i < historyCount; i++) {
            sum += receivedHistory[i];
        }
        return sum / historyCount;
    }
    
    public int getAverageExtractedEnergy() {
        if (historyCount == 0) return 0;
        int sum = 0;
        for (int i = 0; i < historyCount; i++) {
            sum += extractedHistory[i];
        }
        return sum / historyCount;
    }
    
    public int getAverageNetEnergyChange() {
        return getAverageReceivedEnergy() - getAverageExtractedEnergy();
    }
    
    public int getAverageNetEnergyPerSecond() {
        return getAverageNetEnergyChange() * 20;
    }
    
    public void finalizeEnergyTick() {
        receivedLastTick = receivedThisTick;
        extractedLastTick = extractedThisTick;
        
        receivedHistory[historyIndex] = receivedLastTick;
        extractedHistory[historyIndex] = extractedLastTick;
        
        historyIndex = (historyIndex + 1) % ENERGY_AVG_WINDOW;
        historyCount = Math.min(historyCount + 1, ENERGY_AVG_WINDOW);
        
        receivedThisTick = 0;
        extractedThisTick = 0;
    }
    
    public int getClientAverageReceivedEnergy() {
        if (historyCount == 0) return 0;
        
        int sum = 0;
        for (int i = 0; i < historyCount; i++) {
            sum += receivedHistory[i];
        }
        return sum / historyCount;
    }
    
    public int getClientAverageExtractedEnergy() {
        if (historyCount == 0) return 0;
        
        int sum = 0;
        for (int i = 0; i < historyCount; i++) {
            sum += extractedHistory[i];
        }
        return sum / historyCount;
    }
    
    public int getClientAverageNetEnergyChange() {
        return getClientAverageReceivedEnergy()
            - getClientAverageExtractedEnergy();
    }

    public void setEnergy(int amount) {
        energy = Mth.clamp(amount,0, capacity);
        onEnergyChanged();
    }

    public abstract void onEnergyChanged();
    
    /*@Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        
        // mevcut davranış
        tag.putInt("Energy", this.energy);
        
        // history meta
        tag.putInt("HistIdx", historyIndex);
        tag.putInt("HistCount", historyCount);
        
        // arrays
        tag.putIntArray("RecvHist", receivedHistory);
        tag.putIntArray("ExtHist", extractedHistory);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(Tag nbt) {
        
        if (nbt instanceof IntTag intTag) {
            this.energy = intTag.getAsInt();
        }
        
        *//*if (!(nbt instanceof CompoundTag tag)) {
            throw new IllegalArgumentException("Invalid NBT for ModEnergyStorage");
        }*//*
        
        if (nbt instanceof CompoundTag tag) {
            this.energy = tag.getInt("Energy");
            this.historyIndex = tag.getInt("HistIdx");
            this.historyCount = tag.getInt("HistCount");
            
            int[] recv = tag.getIntArray("RecvHist");
            int[] ext = tag.getIntArray("ExtHist");
            
            System.arraycopy(recv, 0, receivedHistory, 0,
                Math.min(recv.length, receivedHistory.length));
            System.arraycopy(ext, 0, extractedHistory, 0,
                Math.min(ext.length, extractedHistory.length));
        }
    }*/
}
