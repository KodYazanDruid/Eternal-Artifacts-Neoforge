package com.sonamorningstar.eternalartifacts.content.enchantment;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.neoforged.neoforge.common.Tags;

public class MagicProtectionEnchantment extends ProtectionEnchantment {
	public MagicProtectionEnchantment() {
		super(Rarity.RARE, Type.ALL, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
	}
	
	@Override
	public int getDamageProtection(int level, DamageSource source) {
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return 0;
		} else if (source.is(Tags.DamageTypes.IS_MAGIC)) {
			return level * 2;
		}
		
		return 0;
	}
}
