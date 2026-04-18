package com.sonamorningstar.eternalartifacts.api.item.armorset;

import com.sonamorningstar.eternalartifacts.api.item.armorset.sets.base.ArmorSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class ArmorSetRegistry {
	public static final Map<ArmorSet, ArmorSetBonus> ARMOR_SET_BONUSES = new HashMap<>();
	public static final Map<ResourceLocation, ArmorSetBonus> ARMOR_SETS = new HashMap<>();
	
	public static void registerArmorSetBonus(ArmorSet armorSet, Consumer<LivingEntity> effect) {
		ArmorSetBonus setBonus = new ArmorSetBonus(armorSet, effect);
		ARMOR_SET_BONUSES.put(armorSet, setBonus);
		ARMOR_SETS.put(armorSet.getKey(), setBonus);
	}
	
	@Nullable
	public static ArmorSetBonus getActiveBonus(LivingEntity entity) {
		List<ItemStack> equippedArmor = new ArrayList<>();
		entity.getArmorSlots().forEach(equippedArmor::add);
		for (Map.Entry<ArmorSet, ArmorSetBonus> entry : ARMOR_SET_BONUSES.entrySet()) {
			if (entry.getKey().canActivate(equippedArmor)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	@Nullable
	public static ArmorSetBonus getBonus(ResourceLocation key) {
		return ARMOR_SETS.get(key);
	}
	
	public record ArmorSetBonus(ArmorSet armorSet, Consumer<LivingEntity> effect) {}
}
