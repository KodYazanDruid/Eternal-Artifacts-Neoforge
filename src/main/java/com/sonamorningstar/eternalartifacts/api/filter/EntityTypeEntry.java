package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

@Getter
@Setter
public class EntityTypeEntry implements EntityFilterEntry {
	@Nullable
	private EntityType<?> filterType;
	private boolean isWhitelist = true;
	
	public static final EntityTypeEntry EMPTY = new EntityTypeEntry(null);
	
	public EntityTypeEntry(@Nullable EntityType<?> filterType) {
		this.filterType = filterType;
	}
	
	@Override
	public boolean matches(Entity entity) {
		if (entity == null) return !isWhitelist;
		if (filterType == null) return !isWhitelist;
		
		boolean result = entity.getType() == filterType;
		return isWhitelist == result;
	}
	
	@Override
	public boolean isEmpty() {
		return filterType == null;
	}
	
	@Override
	public Component getDisplayName() {
		return filterType != null ? filterType.getDescription() : Component.literal("Empty");
	}
	
	@Override
	public boolean isIgnoreNBT() {
		return true;
	}
	
	@Override
	public void setIgnoreNBT(boolean ignoreNBT) {
		// Entity type filtreleme için NBT kontrolü yok
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Type", "Type");
		tag.putBoolean("IsWhitelist", isWhitelist);
		
		if (filterType != null) {
			ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(filterType);
			tag.putString("EntityType", typeId.toString());
		}
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.isWhitelist = tag.getBoolean("IsWhitelist");
		
		if (tag.contains("EntityType")) {
			ResourceLocation typeId = new ResourceLocation(tag.getString("EntityType"));
			this.filterType = BuiltInRegistries.ENTITY_TYPE.get(typeId);
		} else {
			this.filterType = null;
		}
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buff) {
		buff.writeUtf("Type");
		buff.writeBoolean(isWhitelist);
		buff.writeBoolean(filterType != null);
		
		if (filterType != null) {
			buff.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(filterType));
		}
	}
	
	@Override
	public void fromNetwork(FriendlyByteBuf buff) {
		this.isWhitelist = buff.readBoolean();
		boolean hasType = buff.readBoolean();
		
		if (hasType) {
			ResourceLocation typeId = buff.readResourceLocation();
			this.filterType = BuiltInRegistries.ENTITY_TYPE.get(typeId);
		} else {
			this.filterType = null;
		}
	}
	
	@Override
	public String toString() {
		return "EntityTypeEntry{" +
			"filterType=" + (filterType != null ? BuiltInRegistries.ENTITY_TYPE.getKey(filterType) : "null") +
			", isWhitelist=" + isWhitelist +
			'}';
	}
}
