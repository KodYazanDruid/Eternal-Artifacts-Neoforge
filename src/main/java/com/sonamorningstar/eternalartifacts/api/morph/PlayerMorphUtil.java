package com.sonamorningstar.eternalartifacts.api.morph;

import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.CutlassModifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerMorphUtil {
	
	/**
	 * Server-side map of players and their morphed entities.
	 */
	public static final Map<ServerPlayer, EntityType<?>> MORPH_MAP = new HashMap<>();
	private static final Collection<Item> HEADS = CutlassModifier.ENTITY_HEAD_MAP.values();
	
	public static ItemStack getMorphItem(Player player) {
		return CharmManager.findCharm(player, st -> !st.is(Items.DRAGON_HEAD) && HEADS.contains(st.getItem()));
	}
	
	@Nullable
	private static EntityType<? extends LivingEntity> getMorphTypeOther(Player player) {
		/*var helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		var chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		var leggings = player.getItemBySlot(EquipmentSlot.LEGS);
		var boots = player.getItemBySlot(EquipmentSlot.FEET);
		if (helmet.is(ModItems.SHULKER_HELMET) &&
				chestplate.is(ModItems.SHULKER_CHESTPLATE) &&
				leggings.is(ModItems.SHULKER_LEGGINGS) &&
				boots.is(ModItems.SHULKER_BOOTS)) {
			return EntityType.SHULKER;
		}*/
		return null;
	}
	
	public static @org.jetbrains.annotations.Nullable EntityType<? extends LivingEntity> getEntityType(ItemStack headCharm) {
		EntityType<? extends LivingEntity> entityType = null;
		for (var entry : CutlassModifier.ENTITY_HEAD_MAP.entrySet()) {
			if (headCharm.is(entry.getValue())) {
				entityType = entry.getKey();
				if (entityType.is(ModTags.Entities.MORPH_BLACKLISTED)) return null;
			}
		}
		return entityType;
	}
	
	@Nullable
	public static EntityType<? extends LivingEntity> getMorphType(Player player) {
		ItemStack stack = getMorphItem(player);
		if (stack.isEmpty()) return null;
		var other = getMorphTypeOther(player);
		if (other != null) return other;
		return getEntityType(stack);
	}
	
	@Nullable
	public static LivingEntity getMorphEntity(Player player) {
		EntityType<? extends LivingEntity> type = getMorphType(player);
		if (type == null) return null;
		return type.create(player.level());
	}
	
	public static boolean is(Player player, EntityType<?> type) {
		return player instanceof ServerPlayer sp && MORPH_MAP.get(sp) == type;
	}
	
}
