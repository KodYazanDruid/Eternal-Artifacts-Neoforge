package com.sonamorningstar.eternalartifacts.core;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.EnumMap;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Getter
public enum ModArmorMaterials implements ArmorMaterial {
    COMFY("comfy", 6, createDefenceMap(2, 5, 6, 2), 15,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Items.LEATHER)),
    SHULKER("shulker", 9, createDefenceMap(4, 6, 8, 4), 25,
            SoundEvents.SHULKER_BOX_OPEN, 1.5F, 0.1F, () -> Ingredient.of(ModTags.Items.SHULKER_SHELL));

    private final String name;
    @Getter(AccessLevel.NONE)
    private final int durabilityMultiplier;
    @Getter(AccessLevel.NONE)
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    @Getter(AccessLevel.NONE)
    private final Lazy<Ingredient> repairIngredient;

    ModArmorMaterials(
            String pName,
            int pDurabilityMultiplier,
            EnumMap<ArmorItem.Type, Integer> pProtectionFunctionForType,
            int pEnchantmentValue,
            SoundEvent pSound,
            float pToughness,
            float pKnockbackResistance,
            Supplier<Ingredient> pRepairIngredient
    ) {
        this.name = MODID+":"+pName;
        this.durabilityMultiplier = pDurabilityMultiplier;
        this.protectionFunctionForType = pProtectionFunctionForType;
        this.enchantmentValue = pEnchantmentValue;
        this.equipSound = pSound;
        this.toughness = pToughness;
        this.knockbackResistance = pKnockbackResistance;
        this.repairIngredient = Lazy.of(pRepairIngredient);
    }

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 13);
        map.put(ArmorItem.Type.LEGGINGS, 15);
        map.put(ArmorItem.Type.CHESTPLATE, 16);
        map.put(ArmorItem.Type.HELMET, 11);
    });

    private static EnumMap<ArmorItem.Type, Integer> createDefenceMap(int helmet, int chestPlate, int leggings, int boots) {
        EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.HELMET, helmet);
        map.put(ArmorItem.Type.CHESTPLATE, chestPlate);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.BOOTS, boots);
        return map;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type pType) {return HEALTH_FUNCTION_FOR_TYPE.get(pType) * this.durabilityMultiplier;}
    @Override
    public int getDefenseForType(ArmorItem.Type pType) {return this.protectionFunctionForType.get(pType);}
    @Override
    public Ingredient getRepairIngredient() {return this.repairIngredient.get();}
}
