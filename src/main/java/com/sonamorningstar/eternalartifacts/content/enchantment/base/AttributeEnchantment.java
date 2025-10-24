package com.sonamorningstar.eternalartifacts.content.enchantment.base;

import com.mojang.datafixers.util.Either;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	/*public static void mergeTooltips(ItemTooltipEvent event) {
		List<Component> tooltip = event.getToolTip();
		System.out.println("Tooltip event: *-*-*-*-*-*");
		for (Component component : tooltip) {
			System.out.println(component);
		}
	}*/
	
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
