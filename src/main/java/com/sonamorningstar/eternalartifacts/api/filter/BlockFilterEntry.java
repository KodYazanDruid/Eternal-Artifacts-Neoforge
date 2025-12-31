package com.sonamorningstar.eternalartifacts.api.filter;

import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockFilterEntry extends FilterEntry {
	boolean matches(BlockState state);
	
	static BlockFilterEntry fromNBT(CompoundTag tagData) {
		String type = tagData.getString("Type");
		BlockFilterEntry entry = switch (type) {
			case "State" -> new BlockStateEntry(null, true);
			case "Tag" -> new BlockTagEntry(TagKey.create(Registries.BLOCK, new ResourceLocation("dummy:dummy")));
			default -> throw new IllegalArgumentException("Unknown block filter type: " + type);
		};
		entry.deserializeNBT(tagData);
		return entry;
	}
	
	static BlockFilterEntry readFromNetwork(FriendlyByteBuf buff) {
		String type = buff.readUtf();
		BlockFilterEntry entry = switch (type) {
			case "State" -> new BlockStateEntry(null, true);
			case "Tag" -> new BlockTagEntry(TagKey.create(Registries.BLOCK, new ResourceLocation("dummy:dummy")));
			default -> throw new IllegalArgumentException("Unknown block filter type: " + type);
		};
		entry.fromNetwork(buff);
		return entry;
	}
	
	class Empty implements BlockFilterEntry {
		private boolean isWhitelist;
		
		private Empty(boolean isWhitelist) {
			this.isWhitelist = isWhitelist;
		}
		
		public static Empty create(boolean isWhitelist) {
			return new Empty(isWhitelist);
		}
		
		@Override
		public boolean matches(BlockState state) {
			return isWhitelist && state.isAir();
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public Component getDisplayName() {
			return ModConstants.FILTER.withSuffixTranslatable("empty_block");
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
