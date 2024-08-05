package com.sonamorningstar.eternalartifacts.capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModScaleableItemItemStorage extends ModItemItemStorage{

    public ModScaleableItemItemStorage(ItemStack stack, Enchantment enchantment, int multiplier) {
        super(stack, (1 + stack.getEnchantmentLevel(enchantment)) * multiplier);
    }

}
