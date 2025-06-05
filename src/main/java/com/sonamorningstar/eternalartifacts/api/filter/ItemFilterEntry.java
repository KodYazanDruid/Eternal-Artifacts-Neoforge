package com.sonamorningstar.eternalartifacts.api.filter;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface ItemFilterEntry extends FilterEntry, INBTSerializable<CompoundTag> {
	boolean matches(ItemStack stack);
	
	static ItemFilterEntry fromNBT(CompoundTag tagData) {
		String type = tagData.getString("Type");
		ItemFilterEntry entry = switch (type) {
			case "Stack" -> new ItemStackEntry(ItemStack.EMPTY, true);
			case "Tag" -> new ItemTagEntry(TagKey.create(Registries.ITEM, new ResourceLocation("dummy:dummy")));
			default -> throw new IllegalArgumentException("Unknown filter type: " + type);
		};
		entry.deserializeNBT(tagData);
		return entry;
	}
	
	static ItemFilterEntry readFromNetwork(FriendlyByteBuf buff) {
		String type = buff.readUtf();
		ItemFilterEntry entry = switch (type) {
			case "Stack" -> new ItemStackEntry(ItemStack.EMPTY, true);
			case "Tag" -> new ItemTagEntry(TagKey.create(Registries.ITEM, new ResourceLocation("dummy:dummy")));
			default -> throw new IllegalArgumentException("Unknown filter type: " + type);
		};
		entry.fromNetwork(buff);
		return entry;
	}
	
	class Empty implements ItemFilterEntry {
		private boolean isWhitelist;
		
		private Empty(boolean isWhitelist) {
			this.isWhitelist = isWhitelist;
		}
		
		public static Empty create(boolean isWhitelist) {
			return new Empty(isWhitelist);
		}
		
		@Override
		public boolean matches(ItemStack stack) {
			return isWhitelist && stack.isEmpty();
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
		public void setIgnoreNBT(boolean ignoreNBT) {}
		
		@Override
		public void toNetwork(FriendlyByteBuf buff) {
			buff.writeUtf("Stack");
			buff.writeItem(ItemStack.EMPTY);
			buff.writeBoolean(true);
			buff.writeBoolean(isWhitelist);
		}
		
		@Override
		public void fromNetwork(FriendlyByteBuf buff) {
			buff.readItem(); // Read empty stack, but ignore it
			buff.readBoolean(); // Read ignoreNBT, but ignore it
			isWhitelist = buff.readBoolean();
		}
		
		@Override
		public CompoundTag serializeNBT() {
			CompoundTag tag = new CompoundTag();
			tag.putString("Type", "Stack");
			tag.putBoolean("IsWhitelist", isWhitelist);
			return tag;
		}
		
		@Override
		public void deserializeNBT(CompoundTag nbt) {
			isWhitelist = nbt.getBoolean("IsWhitelist");
		}
	}
}