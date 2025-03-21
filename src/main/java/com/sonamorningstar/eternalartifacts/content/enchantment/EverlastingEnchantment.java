package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class EverlastingEnchantment extends Enchantment {
	public EverlastingEnchantment() {
		super(Rarity.RARE, ModEnchantments.ModEnchantmentCategory.EVERLASTING, EquipmentSlot.values());
	}
}
