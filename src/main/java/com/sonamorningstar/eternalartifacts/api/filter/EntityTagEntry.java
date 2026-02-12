package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

@Getter
@Setter
public class EntityTagEntry implements EntityFilterEntry {
	@Nullable
	private TagKey<EntityType<?>> tag;
	private boolean isWhitelist = true;
	
	public EntityTagEntry(@Nullable TagKey<EntityType<?>> tag) {
		this.tag = tag;
	}
	
	@Override
	public boolean matches(Entity entity) {
		if (entity == null) return !isWhitelist;
		if (tag == null) return !isWhitelist;
		
		boolean result = entity.getType().is(tag);
		return isWhitelist == result;
	}
	
	@Override
	public boolean isEmpty() {
		return tag == null;
	}
	
	@Override
	public Component getDisplayName() {
		return tag != null
			? Component.literal("#" + tag.location())
			: Component.literal("Empty");
	}
	
	@Override
	public boolean isIgnoreNBT() {
		return true;
	}
	
	@Override
	public void setIgnoreNBT(boolean ignoreNBT) {
		// Tag filtreleme için NBT kontrolü yok
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("Type", "Tag");
		nbt.putBoolean("IsWhitelist", isWhitelist);
		
		if (tag != null) {
			nbt.putString("Tag", tag.location().toString());
		}
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.isWhitelist = nbt.getBoolean("IsWhitelist");
		
		if (nbt.contains("Tag")) {
			ResourceLocation tagId = new ResourceLocation(nbt.getString("Tag"));
			this.tag = TagKey.create(Registries.ENTITY_TYPE, tagId);
		} else {
			this.tag = null;
		}
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buff) {
		buff.writeUtf("Tag");
		buff.writeBoolean(isWhitelist);
		buff.writeBoolean(tag != null);
		
		if (tag != null) {
			buff.writeResourceLocation(tag.location());
		}
	}
	
	@Override
	public void fromNetwork(FriendlyByteBuf buff) {
		this.isWhitelist = buff.readBoolean();
		boolean hasTag = buff.readBoolean();
		
		if (hasTag) {
			ResourceLocation tagId = buff.readResourceLocation();
			this.tag = TagKey.create(Registries.ENTITY_TYPE, tagId);
		} else {
			this.tag = null;
		}
	}
	
	@Override
	public String toString() {
		return "EntityTagEntry{" +
			"tag=" + (tag != null ? tag.location() : "null") +
			", isWhitelist=" + isWhitelist +
			'}';
	}
}
