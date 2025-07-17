package com.sonamorningstar.eternalartifacts.content.enchantment.base;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

public class MachineEnchantment extends Enchantment {
	public MachineEnchantment(Rarity rarity) {
		super(rarity, ModEnchantments.ModEnchantmentCategory.EMPTY, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
	}
	
	
}
