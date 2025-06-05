package com.sonamorningstar.eternalartifacts.api.filter;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;

@Setter
@Getter
public class FluidStackEntry implements FluidFilterEntry {
	private FluidStack filterStack;
	private boolean ignoreNBT;
	private boolean isWhitelist = true;
	
	public static final FluidStackEntry EMPTY = new FluidStackEntry(FluidStack.EMPTY, true);

	public FluidStackEntry(FluidStack filterStack, boolean ignoreNBT) {
		this.filterStack = filterStack;
		this.ignoreNBT = ignoreNBT;
	}
	
	@Override
	public boolean matches(FluidStack stack) {
		if (filterStack.isEmpty()) {
			return stack.isEmpty();
		}
		return ignoreNBT
			? stack.getFluid() == filterStack.getFluid()
			: stack.isFluidEqual(filterStack);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putString("Type", "Stack");
		tag.put("Stack", filterStack.writeToNBT(new CompoundTag()));
		tag.putBoolean("IgnoreNBT", ignoreNBT);
		tag.putBoolean("IsWhitelist", isWhitelist);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag) {
		this.filterStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("Stack"));
		this.ignoreNBT = tag.getBoolean("IgnoreNBT");
		this.isWhitelist = tag.getBoolean("IsWhitelist");
	}
	
	@Override
	public String toString() {
		return "FluidStackEntry{" +
			"filterStack=" + filterStack +
			", ignoreNBT=" + ignoreNBT +
			", isWhitelist=" + isWhitelist +
			'}';
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buff) {
		buff.writeUtf("Stack");
		buff.writeFluidStack(filterStack);
		buff.writeBoolean(ignoreNBT);
		buff.writeBoolean(isWhitelist);
	}
	
	@Override
	public void fromNetwork(FriendlyByteBuf buff) {
		filterStack = buff.readFluidStack();
		ignoreNBT = buff.readBoolean();
		isWhitelist = buff.readBoolean();
	}
}