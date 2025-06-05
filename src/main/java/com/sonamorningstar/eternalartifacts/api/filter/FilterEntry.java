package com.sonamorningstar.eternalartifacts.api.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface FilterEntry extends INBTSerializable<CompoundTag> {
	boolean isWhitelist();
	void setWhitelist(boolean isWhitelist);
	boolean isIgnoreNBT();
	void setIgnoreNBT(boolean isNbtTolerant);
	default void toggleWhitelist() {
		setWhitelist(!isWhitelist());
	}
	default void toggleIgnoreNbt() {
		setIgnoreNBT(!isIgnoreNBT());
	}
	
	void toNetwork(FriendlyByteBuf buff);
	void fromNetwork(FriendlyByteBuf buff);
}
