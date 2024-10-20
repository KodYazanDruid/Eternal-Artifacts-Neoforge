package com.sonamorningstar.eternalartifacts.core;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Supplier;

public enum ModTiers implements Tier {
    CHLOROPHYTE(3, 1749, 10.0F, 3.0F, 20, ()-> Ingredient.of(ModItems.CHLOROPHYTE_INGOT.get())),
    COPPER(1, 192, 5.0F, 1.5F, 17, () -> Ingredient.of(Tags.Items.INGOTS_COPPER)),
    WITHER(3, 1352, 7.0F, 5.0F, 15, () -> Ingredient.of(Items.WITHER_SKELETON_SKULL));

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Lazy<Ingredient> repairIngredient;

    ModTiers(int pLevel, int pUses, float pSpeed, float pDamage, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
        this.level = pLevel;
        this.uses = pUses;
        this.speed = pSpeed;
        this.damage = pDamage;
        this.enchantmentValue = pEnchantmentValue;
        this.repairIngredient = Lazy.of(pRepairIngredient);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
