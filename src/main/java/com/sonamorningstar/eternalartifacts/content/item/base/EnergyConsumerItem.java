package com.sonamorningstar.eternalartifacts.content.item.base;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.capabilities.Capabilities;

public class EnergyConsumerItem extends EnergyRendererItem {
	
	public EnergyConsumerItem(Properties props) {
		super(props);
	}
	
	@Override
	public void setDamage(ItemStack stack, int damage) {
		//super.setDamage(stack, damage);
		var cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (cap != null) {
			int amount = cap.getMaxEnergyStored() - damage;
			consumeEnergy(stack, amount);
		} else {
			EternalArtifacts.LOGGER.error("{} extends {} but doesn't have an energy capability!", this.getClass(), EnergyConsumerItem.class);
		}
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		//return super.getMaxDamage(stack);
		var cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (cap != null) return cap.getMaxEnergyStored();
		else {
			EternalArtifacts.LOGGER.error("{} extends {} but doesn't have an energy capability!", this.getClass(), EnergyConsumerItem.class);
			return 0;
		}
	}
	
	public boolean consumeEnergy(ItemStack stack, int amount) {
		var cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (cap instanceof ModEnergyStorage mes) {
			int extracted = mes.extractEnergyForced(amount, true);
			if (extracted == amount) {
				mes.extractEnergyForced(applyUnbreakingBonus(stack.getEnchantmentLevel(Enchantments.UNBREAKING), amount), false);
				return true;
			}
		}
		return false;
	}
	
	private int applyUnbreakingBonus(int lvl, int actualAmount) {
		if (lvl <= 0) return actualAmount;
		double reductionFactor = Math.pow(0.9, lvl);
		return (int) Math.round(actualAmount * reductionFactor);
	}
}
