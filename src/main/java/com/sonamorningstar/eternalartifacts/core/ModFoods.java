package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties ORANGE = new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build();
    public static final FoodProperties BANANA = new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build();
    public static final FoodProperties ANCIENT_FRUIT = new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build();
    public static final FoodProperties MEAT_INGOT = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).meat().build();
    public static final FoodProperties COOKED_MEAT_INGOT = new FoodProperties.Builder().nutrition(8).saturationMod(0.8f).meat().build();
    public static final FoodProperties APPLE_PIE = new FoodProperties.Builder().nutrition(8).saturationMod(0.3f).build();
    public static final FoodProperties BANANA_CREAM_PIE = new FoodProperties.Builder().nutrition(8).saturationMod(0.3f).build();
    public static final FoodProperties DUCK_MEAT = new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).meat().build();
    public static final FoodProperties COOKED_DUCK_MEAT = new FoodProperties.Builder().nutrition(6).saturationMod(0.6f).meat().build();
    public static final FoodProperties BANANA_BREAD = new FoodProperties.Builder().nutrition(7).saturationMod(0.7f).build();
    public static final FoodProperties GREEN_APPLE = new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build();
    public static final FoodProperties YELLOW_APPLE = new FoodProperties.Builder().nutrition(4).saturationMod(0.3F).build();
    
    public static final FoodProperties GOLDEN_ANCIENT_FRUIT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(1.2F)
            .effect(()->new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 1), 1.0F)
            .effect(()->new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 1), 1.0F)
            .alwaysEat()
            .build();
    public static final FoodProperties ENCHANTED_GOLDEN_ANCIENT_FRUIT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(1.2F)
            .effect(()->new MobEffectInstance(ModEffects.FLIGHT.get(), 2400, 0), 1.0F)
            .effect(()->new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 2), 1.0F)
            .effect(()->new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 2), 1.0F)
            .effect(()->new MobEffectInstance(MobEffects.ABSORPTION, 2400, 4), 1.0F)
            .alwaysEat()
            .build();
    public static final FoodProperties ANGELIC_HEART = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(1.2F)
            .effect(()->new MobEffectInstance(MobEffects.REGENERATION, 2400, 1), 1.0F)
            .effect(()->new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 1), 1.0F)
            .effect(()->new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2400, 1), 1.0F)
            .alwaysEat()
            .build();
}
