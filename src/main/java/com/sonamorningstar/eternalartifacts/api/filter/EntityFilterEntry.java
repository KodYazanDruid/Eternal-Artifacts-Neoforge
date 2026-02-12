package com.sonamorningstar.eternalartifacts.api.filter;

import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;

public interface EntityFilterEntry extends FilterEntry {
	boolean matches(Entity entity);
	
	static EntityFilterEntry fromNBT(CompoundTag tagData) {
		String type = tagData.getString("Type");
		EntityFilterEntry entry = switch (type) {
			case "Predicate" -> new EntityPredicateEntry();
			case "Type" -> new EntityTypeEntry(null);
			case "Tag" -> new EntityTagEntry(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("dummy:dummy")));
			default -> throw new IllegalArgumentException("Unknown entity filter type: " + type);
		};
		entry.deserializeNBT(tagData);
		return entry;
	}
	
	static EntityFilterEntry readFromNetwork(FriendlyByteBuf buff) {
		String type = buff.readUtf();
		EntityFilterEntry entry = switch (type) {
			case "Predicate" -> new EntityPredicateEntry();
			case "Type" -> new EntityTypeEntry(null);
			case "Tag" -> new EntityTagEntry(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("dummy:dummy")));
			default -> throw new IllegalArgumentException("Unknown entity filter type: " + type);
		};
		entry.fromNetwork(buff);
		return entry;
	}
	
	class Empty implements EntityFilterEntry {
		private boolean isWhitelist;
		
		private Empty(boolean isWhitelist) {
			this.isWhitelist = isWhitelist;
		}
		
		public static Empty create(boolean isWhitelist) {
			return new Empty(isWhitelist);
		}
		
		@Override
		public boolean matches(Entity entity) {
			return !isWhitelist;
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public Component getDisplayName() {
			return ModConstants.FILTER.withSuffixTranslatable("empty_entity");
		}
		
		@Override
		public boolean isWhitelist() {
			return isWhitelist;
		}
		
		@Override
		public void setWhitelist(boolean isWhitelist) {
			this.isWhitelist = isWhitelist;
		}
		
		@Override
		public boolean isIgnoreNBT() {
			return true;
		}
		
		@Override
		public void setIgnoreNBT(boolean isNbtTolerant) {}
		
		@Override
		public CompoundTag serializeNBT() {
			CompoundTag tag = new CompoundTag();
			tag.putString("Type", "Empty");
			tag.putBoolean("IsWhitelist", isWhitelist);
			return tag;
		}
		
		@Override
		public void deserializeNBT(CompoundTag tag) {
			this.isWhitelist = tag.getBoolean("IsWhitelist");
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buff) {
			buff.writeUtf("Empty");
			buff.writeBoolean(isWhitelist);
		}
		
		@Override
		public void fromNetwork(FriendlyByteBuf buff) {
			this.isWhitelist = buff.readBoolean();
		}
	}
}
