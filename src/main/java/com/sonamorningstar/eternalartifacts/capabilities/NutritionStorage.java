package com.sonamorningstar.eternalartifacts.capabilities;

import com.sonamorningstar.eternalartifacts.capabilities.handler.INutritionHandler;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.text.DecimalFormat;

public class NutritionStorage implements INutritionHandler, INBTSerializable<CompoundTag> {
    private int nutrition = 0;
    private float saturation = 0;
    private final int maxNutrition;
    private final float maxSaturation;
    private final boolean canFillNut;
    private final boolean canDrainNut;
    private final boolean canFillSat;
    private final boolean canDrainSat;

    public NutritionStorage(int maxNutrition, float maxSaturation) {
        this(maxNutrition, maxSaturation, true, true, true, true);
    }

    public NutritionStorage(int maxNutrition, float maxSaturation, boolean canFillNut, boolean canDrainNut, boolean canFillSat, boolean canDrainSat) {
        this.maxNutrition = maxNutrition;
        this.maxSaturation = maxSaturation;
        this.canFillNut = canFillNut;
        this.canDrainNut = canDrainNut;
        this.canFillSat = canFillSat;
        this.canDrainSat = canDrainSat;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Nutrition", nutrition);
        tag.putFloat("Saturation", saturation);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.nutrition = nbt.getInt("Nutrition");
        this.saturation = nbt.getFloat("Saturation");
    }

    @Override
    public int fillNutrition(int amount, boolean simulate) {
        int inserted = Math.min(amount, maxNutrition - nutrition);
        if (inserted <= 0) return 0;
        if (!simulate) {
            this.nutrition += inserted;
            onChange(Type.NUTRITION);
        }
        return inserted;
    }

    @Override
    public int drainNutrition(int amount, boolean simulate) {
        int drained = Math.min(amount, nutrition);
        if (drained <= 0) return 0;
        if (!simulate) {
            this.nutrition -= drained;
            onChange(Type.NUTRITION);
        }
        return drained;
    }

    @Override
    public float fillSaturation(float amount, boolean simulate) {
        float inserted = Math.min(amount, maxSaturation - saturation);
        if (inserted <= 0) return 0;
        if (!simulate) {
            this.saturation += inserted;
            onChange(Type.SATURATION);
        }
        return inserted;
    }

    @Override
    public float drainSaturation(float amount, boolean simulate) {
        float drained = Math.min(amount, saturation);
        if (drained <= 0) return 0;
        if (!simulate) {
            DecimalFormat decimal = new DecimalFormat("#.#");
            decimal.format(drained);
            this.saturation -= drained;
            onChange(Type.SATURATION);
        }
        return drained;
    }

    @Override
    public int getNutritionAmount() {
        return nutrition;
    }

    @Override
    public float getSaturationAmount() {
        return (float) (Math.floor(saturation * 10) * .1f);
    }

    @Override
    public int getMaxNutritionAmount() {
        return maxNutrition;
    }

    @Override
    public float getMaxSaturationAmount() {
        return maxSaturation;
    }

    @Override
    public float getSaturationMod() {
        return getSaturationAmount() / (2.0F * getNutritionAmount());
    }

    public float calculateSatMod(int nutrition) {
        return getNutritionAmount() / (2.0F * nutrition);
    }

    @Override
    public boolean canFillNutrition() {
        return canFillNut;
    }

    @Override
    public boolean canDrainNutrition() {
        return canDrainNut;
    }

    @Override
    public boolean canFillSaturation() {
        return canFillSat;
    }

    @Override
    public boolean canDrainSaturation() {
        return canDrainSat;
    }

    protected void onChange(Type type) {}

    public enum Type {
        NUTRITION("Nutrition"),
        SATURATION("Saturation");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
