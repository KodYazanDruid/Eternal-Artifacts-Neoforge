package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.content.item.ChiselItem;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class VersatilityEnchantment extends Enchantment {
    public static final List<Class<?>> acceptedItems = List.of(
            PickaxeItem.class,
            ChiselItem.class
    );

    public VersatilityEnchantment() {
        super(Rarity.VERY_RARE, ModEnchantments.ModEnchantmentCategory.VERSATILITY, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int pEnchantmentLevel) {
        return 30;
    }
    @Override
    public int getMaxCost(int pEnchantmentLevel) {
        return 50;
    }

    @Override
    public boolean isTreasureOnly() {return true;}
    @Override
    public boolean isTradeable() {return false;}
}
