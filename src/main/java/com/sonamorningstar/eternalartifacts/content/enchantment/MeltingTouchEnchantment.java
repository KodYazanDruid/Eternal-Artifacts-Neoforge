package com.sonamorningstar.eternalartifacts.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MeltingTouchEnchantment extends Enchantment {
	public MeltingTouchEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMinCost(int lvl) {
		return 15;
	}
	
	@Override
	public int getMaxCost(int lvl) {
		return super.getMinCost(lvl) + 50;
	}
}
