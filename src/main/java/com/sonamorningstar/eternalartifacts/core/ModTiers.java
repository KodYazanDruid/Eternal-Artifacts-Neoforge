package com.sonamorningstar.eternalartifacts.core;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.function.Supplier;

@Getter
public enum ModTiers implements Tier {
    CHLOROPHYTE(3, 1749, 10.0F, 3.0F, 20, ()-> Ingredient.of(ModItems.CHLOROPHYTE_INGOT.get())),
    COPPER(1, 192, 5.5F, 1.5F, 17, () -> Ingredient.of(Tags.Items.INGOTS_COPPER)),
    WITHER(3, 1352, 7.0F, 5.0F, 15, () -> Ingredient.of(Items.WITHER_SKELETON_SKULL)),
    STEEL(2, 512, 6.0F, 2.5F, 16, () -> Ingredient.of(ModTags.Items.INGOTS_STEEL)),
    BONE(1, 160, 6.0F, 2.5F, 8, () -> Ingredient.of(Tags.Items.BONES));

    private final int level;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    @Getter(AccessLevel.NONE)
    private final Lazy<Ingredient> repairIngredient;

    ModTiers(int pLevel, int pUses, float pSpeed, float pDamage, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
        this.level = pLevel;
        this.uses = pUses;
        this.speed = pSpeed;
        this.attackDamageBonus = pDamage;
        this.enchantmentValue = pEnchantmentValue;
        this.repairIngredient = Lazy.of(pRepairIngredient);
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
