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
	public FortificationEnchantment(Rarity pRarity, Set<Attribute> attributeSet, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
		super(pRarity, 3, attributeSet, pCategory, pApplicableSlots);
	}
	
	@Nullable
	@Override
	public AttributeModifier getModifier(Attribute attribute, EquipmentSlot slot, ItemStack stack, int level) {
		if (!(stack.getItem() instanceof ArmorItem armor) || armor.getType().getSlot() != slot || !hasSlot(slot)) return null;
		String cleanedDesc = attrCleanName.computeIfAbsent(attribute, attr -> attr.getDescriptionId().replaceAll("\\.", ""));
		String uuidString = MODID + cleanedDesc + slot.getName() + level;
		float amount = 0;
		if (attribute == Attributes.ARMOR) amount = 1;
		else if (attribute == Attributes.ARMOR_TOUGHNESS) amount = 0.5F;
		return new AttributeModifier(UUID.nameUUIDFromBytes(uuidString.getBytes(StandardCharsets.UTF_8)), cleanedDesc, amount * level, AttributeModifier.Operation.ADDITION);
	}
}
