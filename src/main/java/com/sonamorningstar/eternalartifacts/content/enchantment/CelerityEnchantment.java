package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.content.enchantment.base.MachineEnchantment;

public class CelerityEnchantment extends MachineEnchantment {
	public CelerityEnchantment() {
		super(Rarity.UNCOMMON);
	}
	
	@Override
	public int getMinCost(int pEnchantmentLevel) {
		return 1 + 10 * (pEnchantmentLevel - 1);
	}
	
	@Override
	public int getMaxCost(int pEnchantmentLevel) {
		return super.getMinCost(pEnchantmentLevel) + 50;
	}
	
	@Override
	public int getMaxLevel() {
		return 3;
	}
}
