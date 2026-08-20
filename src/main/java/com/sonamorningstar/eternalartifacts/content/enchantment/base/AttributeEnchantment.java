package com.sonamorningstar.eternalartifacts.content.enchantment.base;

import lombok.Getter;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Getter
public abstract class AttributeEnchantment extends Enchantment {
	public static Map<Attribute, String> attrCleanName = new HashMap<>();
	protected final Set<Attribute> attributeSet;
	protected final EquipmentSlot[] applicableSlots;
	private final int maxLevel;
	
	public AttributeEnchantment(Rarity pRarity, int maxLevel, Set<Attribute> attributeSet, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
		super(pRarity, pCategory, pApplicableSlots);
		this.maxLevel = maxLevel;
		this.attributeSet = attributeSet;
		this.applicableSlots = pApplicableSlots;
	}
	
	@Nullable
	public abstract AttributeModifier getModifier(Attribute attribute, EquipmentSlot slot, ItemStack stack, int level);
	
	public String getName(Attribute attribute) {
		return attrCleanName.computeIfAbsent(attribute, attr -> attr.getDescriptionId().replaceAll("\\.", ""));
	}
	
	public UUID getID(Attribute attribute, String extra, int level) {
		String uuidString = MODID + getName(attribute) + extra + level;
		return UUID.nameUUIDFromBytes(uuidString.getBytes(StandardCharsets.UTF_8));
	}
	
	protected boolean hasSlot(EquipmentSlot slot) {
		if (applicableSlots.length == 0) return true;
		for (EquipmentSlot s : applicableSlots) {
			if (s == slot) return true;
		}
		return false;
	}
	
	@Override
	public int getMaxLevel() {
		return maxLevel;
	}
	
	@Override
	public int getMinCost(int pLevel) {
		return 5 + (pLevel - 1) * 11;
	}
	
	@Override
	public int getMaxCost(int pLevel) {
		return getMinCost(pLevel) + 20;
	}
}
