package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.content.enchantment.base.AttributeEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class FortificationEnchantment extends AttributeEnchantment {
	public FortificationEnchantment(Rarity pRarity, int maxLevel, Set<Attribute> attributeSet, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
		super(pRarity, maxLevel, attributeSet, pCategory, pApplicableSlots);
	}
	
	@Nullable
	@Override
	public AttributeModifier getModifier(Attribute attribute, EquipmentSlot slot, ItemStack stack, int level) {
		if (!(stack.getItem() instanceof ArmorItem armor) || armor.getType().getSlot() != slot || !hasSlot(slot)) return null;
		float amount = 0;
		if (attribute == Attributes.ARMOR) amount = 1;
		else if (attribute == Attributes.ARMOR_TOUGHNESS) amount = 0.5F;
		return new AttributeModifier(getID(attribute, slot.getName(), level), getName(attribute), amount * level, AttributeModifier.Operation.ADDITION);
	}
}
