package com.sonamorningstar.eternalartifacts.capabilities.handler;

public interface INutritionHandler {
    int fillNutrition(int amount, boolean simulate);
    int drainNutrition(int amount, boolean simulate);
    float fillSaturation(float amount, boolean simulate);
    float drainSaturation(float amount, boolean simulate);

    int getNutritionAmount();
    float getSaturationAmount();
    int getMaxNutritionAmount();
    float getMaxSaturationAmount();
    float getSaturationMod();

    boolean canFillNutrition();
    boolean canDrainNutrition();
    boolean canFillSaturation();
    boolean canDrainSaturation();
}
