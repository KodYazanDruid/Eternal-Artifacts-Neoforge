package com.sonamorningstar.eternalartifacts.api.machine.config;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@Setter
@Getter
public class AutoTransferConfig implements Config {
	private boolean input;
	private boolean output;
	private final String tagKey;
	public final ResourceLocation location;
	
	public AutoTransferConfig() {
		this.location = ConfigLocations.getConfigLocation(this.getClass());
		this.tagKey = location.getPath();
	}
	
	@Override
	public void save(CompoundTag tag) {
		CompoundTag auto = new CompoundTag();
		auto.putBoolean("AutoInput", input);
		auto.putBoolean("AutoOutput", output);
		tag.put(tagKey, auto);
	}
	
	@Override
	public void load(CompoundTag tag) {
		CompoundTag tagAuto = tag.getCompound(tagKey);
		input = tagAuto.getBoolean("AutoInput");
		output = tagAuto.getBoolean("AutoOutput");
	}
	
	@Override
	public void writeToServer(FriendlyByteBuf buf) {
		buf.writeBoolean(input);
		buf.writeBoolean(output);
	}
	
	@Override
	public void readFromClient(FriendlyByteBuf buf) {
		input = buf.readBoolean();
		output = buf.readBoolean();
	}
	
	@Override
	public String toString() {
		return "AutoOutputConfig{" +
			"input=" + input +
			", output=" + output +
			", tagKey='" + tagKey + '\'' +
			'}';
	}
}