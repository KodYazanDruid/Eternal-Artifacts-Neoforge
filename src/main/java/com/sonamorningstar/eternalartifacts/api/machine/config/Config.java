package com.sonamorningstar.eternalartifacts.api.machine.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public interface Config {
	void save(CompoundTag tag);
	
	void load(CompoundTag tag);
	
	String getTagKey();
	
	ResourceLocation getLocation();
	
	void writeToServer(FriendlyByteBuf buf);
	
	void readFromClient(FriendlyByteBuf buf);
	
	//void collectTooltips(List<Component> tooltips, TooltipFlag flag);
}