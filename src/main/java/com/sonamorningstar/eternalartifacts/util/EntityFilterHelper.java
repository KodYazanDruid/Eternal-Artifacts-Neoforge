package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.api.filter.EntityTagEntry;
import com.sonamorningstar.eternalartifacts.api.filter.EntityTypeEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public final class EntityFilterHelper {
	
	private EntityFilterHelper() {}
	
	public static void saveTypeEntries(CompoundTag tag, List<EntityTypeEntry> entries) {
		if (entries.isEmpty()) {
			tag.remove("EntityTypeEntries");
			return;
		}
		ListTag list = new ListTag();
		for (EntityTypeEntry entry : entries) {
			list.add(entry.serializeNBT());
		}
		tag.put("EntityTypeEntries", list);
	}
	
	public static void loadTypeEntries(CompoundTag tag, List<EntityTypeEntry> entries) {
		entries.clear();
		if (tag.contains("EntityTypeEntries", Tag.TAG_LIST)) {
			ListTag list = tag.getList("EntityTypeEntries", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				EntityTypeEntry entry = new EntityTypeEntry(null);
				entry.deserializeNBT(list.getCompound(i));
				if (!entry.isEmpty() && !entries.contains(entry)) entries.add(entry);
			}
		}
	}
	
	public static void saveTagEntries(CompoundTag tag, List<EntityTagEntry> entries) {
		if (entries.isEmpty()) {
			tag.remove("EntityTagEntries");
			return;
		}
		ListTag list = new ListTag();
		for (EntityTagEntry entry : entries) {
			list.add(entry.serializeNBT());
		}
		tag.put("EntityTagEntries", list);
	}
	
	public static void loadTagEntries(CompoundTag tag, List<EntityTagEntry> entries) {
		entries.clear();
		if (tag.contains("EntityTagEntries", Tag.TAG_LIST)) {
			ListTag list = tag.getList("EntityTagEntries", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				EntityTagEntry entry = new EntityTagEntry(null);
				entry.deserializeNBT(list.getCompound(i));
				if (!entry.isEmpty() && !entries.contains(entry)) entries.add(entry);
			}
		}
	}
	
	// === Network ===
	
	public static void writeTypeEntries(FriendlyByteBuf buf, List<EntityTypeEntry> entries) {
		buf.writeVarInt(entries.size());
		for (EntityTypeEntry entry : entries) {
			entry.toNetwork(buf);
		}
	}
	
	public static List<EntityTypeEntry> readTypeEntries(FriendlyByteBuf buf) {
		int count = buf.readVarInt();
		List<EntityTypeEntry> entries = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			EntityTypeEntry entry = new EntityTypeEntry(null);
			buf.readUtf(); // skip "Type" prefix
			entry.fromNetwork(buf);
			if (!entry.isEmpty() && !entries.contains(entry)) entries.add(entry);
		}
		return entries;
	}
	
	public static void writeTagEntries(FriendlyByteBuf buf, List<EntityTagEntry> entries) {
		buf.writeVarInt(entries.size());
		for (EntityTagEntry entry : entries) {
			entry.toNetwork(buf);
		}
	}
	
	public static List<EntityTagEntry> readTagEntries(FriendlyByteBuf buf) {
		int count = buf.readVarInt();
		List<EntityTagEntry> entries = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			EntityTagEntry entry = new EntityTagEntry(null);
			buf.readUtf(); // skip "Tag" prefix
			entry.fromNetwork(buf);
			if (!entry.isEmpty() && !entries.contains(entry)) entries.add(entry);
		}
		return entries;
	}
	
	// === String Parsing ===
	
	/**
	 * Parses a user input string into an EntityTypeEntry or EntityTagEntry.
	 * If the string starts with '#', it's treated as an entity tag.
	 * Otherwise, it's treated as an entity type resource location.
	 * @return Object[]{EntityTypeEntry or EntityTagEntry, Boolean isTag} or null if invalid
	 */
	public static Object parseFilterInput(String input) {
		if (input == null || input.isBlank()) return null;
		input = input.trim();
		
		if (input.startsWith("#")) {
			String tagStr = input.substring(1);
			ResourceLocation tagId = ResourceLocation.tryParse(tagStr);
			if (tagId == null) return null;
			return new EntityTagEntry(TagKey.create(Registries.ENTITY_TYPE, tagId));
		} else {
			ResourceLocation typeId = ResourceLocation.tryParse(input);
			if (typeId == null) return null;
			if (!BuiltInRegistries.ENTITY_TYPE.containsKey(typeId)) return null;
			EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(typeId);
			return new EntityTypeEntry(type);
		}
	}
}
