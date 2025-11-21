package com.sonamorningstar.eternalartifacts.api.machine.config;

import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;

@Getter
public class SideConfig implements Config {
	public enum TransferType {DEFAULT, NONE, PULL, PUSH}
	
	public final ResourceLocation location;
	private final String tagKey;
	
	private final EnumMap<Direction, TransferType> sides = new EnumMap<>(Direction.class);
	
	
	public SideConfig() {
		this.location = ConfigLocations.getConfigLocation(this.getClass());
		this.tagKey = location.getPath();
	}
	
	public TransferType cycleNext(Direction side) {
		TransferType type = sides.getOrDefault(side, TransferType.DEFAULT);
		TransferType next = switch (type) {
			case DEFAULT -> TransferType.NONE;
			case NONE -> TransferType.PULL;
			case PULL -> TransferType.PUSH;
			case PUSH -> TransferType.DEFAULT;
		};
		sides.put(side, next);
		return next;
	}
	
	public TransferType cyclePrev(Direction side) {
		TransferType type = sides.getOrDefault(side, TransferType.DEFAULT);
		TransferType prev = switch (type) {
			case DEFAULT -> TransferType.PUSH;
			case NONE -> TransferType.DEFAULT;
			case PULL -> TransferType.NONE;
			case PUSH -> TransferType.PULL;
		};
		sides.put(side, prev);
		return prev;
	}
	
	@Override
	public void save(CompoundTag tag) {
		CompoundTag sidesTag = new CompoundTag();
		for (Direction dir : Direction.values()) {
			sidesTag.putInt(dir.getName(), sides.getOrDefault(dir, TransferType.DEFAULT).ordinal());
		}
		tag.put(tagKey, sidesTag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		sides.clear();
		CompoundTag sidesTag = tag.getCompound(tagKey);
		for (Direction dir : Direction.values()) {
			if (sidesTag.contains(dir.getName(), Tag.TAG_INT)) {
				sides.put(dir, TransferType.values()[sidesTag.getInt(dir.getName())]);
			}
		}
	}
	
	@Override
	public void writeToServer(FriendlyByteBuf buf) {
		for (Direction dir : Direction.values()) {
			buf.writeEnum(sides.getOrDefault(dir, TransferType.DEFAULT));
		}
	}
	
	@Override
	public void readFromClient(FriendlyByteBuf buf) {
		sides.clear();
		for (Direction dir : Direction.values()) {
			sides.put(dir, buf.readEnum(TransferType.class));
		}
	}
	
	@Override
	public String toString() {
		return "SideConfig{" +
			"sides=" + sides +
			", tagKey='" + tagKey + '\'' +
			'}';
	}
}