package com.sonamorningstar.eternalartifacts.api.filter;

import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidFilterEntry extends FilterEntry {
	boolean matches(FluidStack stack);
	
	static FluidFilterEntry fromNBT(CompoundTag tagData) {
		String type = tagData.getString("Type");
		FluidFilterEntry entry = switch (type) {
			case "Stack" -> new FluidStackEntry(FluidStack.EMPTY, true);
			case "Tag" -> new FluidTagEntry(TagKey.create(Registries.FLUID, new ResourceLocation("dummy:dummy")));
			default -> throw new IllegalArgumentException("Unknown filter type: " + type);
		};
		entry.deserializeNBT(tagData);
		return entry;
	}
	
	static FluidFilterEntry readFromNetwork(FriendlyByteBuf buff) {
		String type = buff.readUtf();
		FluidFilterEntry entry = switch (type) {
			case "Stack" -> new FluidStackEntry(FluidStack.EMPTY, true);
			case "Tag" -> new FluidTagEntry(TagKey.create(Registries.FLUID, new ResourceLocation("dummy:dummy")));
			default -> throw new IllegalArgumentException("Unknown filter type: " + type);
		};
		entry.fromNetwork(buff);
		return entry;
	}
	
	class Empty implements FluidFilterEntry {
		private boolean isWhitelist;
		
		private Empty(boolean isWhitelist) {
			this.isWhitelist = isWhitelist;
		}
		
		public static Empty create(boolean isWhitelist) {
			return new Empty(isWhitelist);
		}
		
		@Override
		public boolean matches(FluidStack stack) {
			return isWhitelist && stack.isEmpty();
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public Component getDisplayName() {
			return ModConstants.FILTER.withSuffixTranslatable("empty_fluid");
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
			return false;
		}
		
		@Override
		public void setIgnoreNBT(boolean isNbtTolerant) {}
		
		@Override
		public void toNetwork(FriendlyByteBuf buff) {
			buff.writeUtf("Stack");
			buff.writeFluidStack(FluidStack.EMPTY);
			buff.writeBoolean(true);
			buff.writeBoolean(isWhitelist);
		}
		
		@Override
		public void fromNetwork(FriendlyByteBuf buff) {
			buff.readFluidStack(); // Read fluid stack, but ignore it
			buff.readBoolean(); // Read ignoreNBT, but ignore it
			this.isWhitelist = buff.readBoolean();
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
			this.isWhitelist = nbt.getBoolean("IsWhitelist");
		}
	}
}
