package com.sonamorningstar.eternalartifacts.api.item.armorset.sets.base;

import com.google.common.collect.Multimap;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import java.util.List;

@Getter
public class AttributeArmorSet extends ArmorSet {
	private final Multimap<Attribute, AttributeModifier> modifiers;
	public AttributeArmorSet(ResourceLocation key, List<Item> armorPieces, Multimap<Attribute, AttributeModifier> modifiers) {
		super(key, armorPieces);
		this.modifiers = modifiers;
		this.hasDescription = false;
	}
	
}
