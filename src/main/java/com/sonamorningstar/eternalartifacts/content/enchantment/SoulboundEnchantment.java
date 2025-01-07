package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.BindingCurseEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

public class SoulboundEnchantment extends Enchantment {
    public SoulboundEnchantment() {
        super(Rarity.VERY_RARE, ModEnchantments.ModEnchantmentCategory.SOULBOUND, EquipmentSlot.values());
    }

    public static boolean has(ItemStack stack) {
        return stack.getEnchantmentLevel(ModEnchantments.SOULBOUND.get()) > 0;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other) && !(other instanceof BindingCurseEnchantment);
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
