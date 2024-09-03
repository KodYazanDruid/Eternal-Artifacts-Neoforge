package com.sonamorningstar.eternalartifacts.capabilities;

import com.sonamorningstar.eternalartifacts.capabilities.handler.IHeatHandler;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class HeatStorage implements IHeatHandler, INBTSerializable<CompoundTag> {
    private final int capacity;
    private int heat;
    private boolean canHeat;
    private boolean canCool;

    public HeatStorage(int capacity) {
        this(capacity, true, true);
    }

    public HeatStorage(int capacity, boolean canHeat, boolean canCool) {
        this.capacity = capacity;
    }

    @Override
    public int heat(int amount, boolean simulate) {
        int heated = Math.min(capacity - heat, amount);
        if (heated <= 0) return 0;
        if (!simulate) {
            heat += heated;
            onHeat();
        }
        return heated;
    }

    @Override
    public int cool(int amount, boolean simulate) {
        int cooled = Math.min(heat, amount);
        if (cooled <= 0) return 0;
        if (!simulate) {
            heat -= cooled;
            onCool();
        }
        return cooled;
    }

    @Override
    public int getHeat() {return heat;}
    @Override
    public int getMaxHeat() {return capacity;}

    @Override
    public boolean canHeat() {return canHeat;}
    @Override
    public boolean setCanHeat(boolean flag) {return canHeat = flag;}
    @Override
    public boolean canCool() {return canCool;}
    @Override
    public boolean setCanCool(boolean flag) {return canCool = flag;}

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Heat", heat);
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag tag) {
        heat = tag.getInt("Heat");
    }

    public void onHeat() {onChange();}
    public void onCool() {onChange();}
    public void onChange() {}
}
