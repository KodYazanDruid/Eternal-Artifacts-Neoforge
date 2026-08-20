package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.content.enchantment.base.AttributeEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class RapidHitEnchantment extends AttributeEnchantment {
	public RapidHitEnchantment(Rarity pRarity, int maxLevel, Set<Attribute> attributeSet, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
		super(pRarity, maxLevel, attributeSet, pCategory, pApplicableSlots);
	}
	
	@Nullable
	@Override
	public AttributeModifier getModifier(Attribute attribute, EquipmentSlot slot, ItemStack stack, int level) {
		if (!hasSlot(slot)) return null;
		return new AttributeModifier(getID(attribute, slot.getName(), level), getName(attribute), 0.2 * level, AttributeModifier.Operation.MULTIPLY_BASE);
	}
}
