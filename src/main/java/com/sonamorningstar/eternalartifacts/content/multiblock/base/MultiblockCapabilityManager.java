package com.sonamorningstar.eternalartifacts.content.multiblock.base;

import com.sonamorningstar.eternalartifacts.client.render.util.DirectionRotationCache;
import com.sonamorningstar.eternalartifacts.util.RelativeBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class MultiblockCapabilityManager {
	
	public enum CapabilityType {
		ENERGY, FLUID, ITEM
	}
	
	public final Map<RelativeBlockPos, Map<Direction, Set<CapabilityType>>> blockCapabilities = new HashMap<>();
	
	public MultiblockCapabilityManager addCapability(int xOffset, int yOffset, int zOffset, Direction dir, CapabilityType type) {
		RelativeBlockPos pos = new RelativeBlockPos(xOffset, yOffset, zOffset);
		blockCapabilities.computeIfAbsent(pos, k -> new EnumMap<>(Direction.class))
			.computeIfAbsent(dir, k -> EnumSet.noneOf(CapabilityType.class))
			.add(type);
		return this;
	}
	
	public MultiblockCapabilityManager addCapabilityAllDirections(int xOffset, int yOffset, int zOffset, CapabilityType type) {
		for (Direction dir : Direction.values()) {
			addCapability(xOffset, yOffset, zOffset, dir, type);
		}
		return this;
	}
	
	public MultiblockCapabilityManager addCapability(int xOffset, int yOffset, int zOffset, CapabilityType type, Direction... directions) {
		for (Direction dir : directions) {
			addCapability(xOffset, yOffset, zOffset, dir, type);
		}
		return this;
	}
	
	public MultiblockCapabilityManager addAllCapabilities(int xOffset, int yOffset, int zOffset, Direction dir) {
		RelativeBlockPos pos = new RelativeBlockPos(xOffset, yOffset, zOffset);
		blockCapabilities.computeIfAbsent(pos, k -> new EnumMap<>(Direction.class))
			.put(dir, EnumSet.allOf(CapabilityType.class));
		return this;
	}
	
	public MultiblockCapabilityManager addAllCapabilitiesAllDirections(int xOffset, int yOffset, int zOffset) {
		for (Direction dir : Direction.values()) {
			addAllCapabilities(xOffset, yOffset, zOffset, dir);
		}
		return this;
	}
	
	public boolean hasCapability(BlockPos frontLeftTopPos, BlockPos targetPos, Direction dir,
								 CapabilityType type, Direction forwards, Direction upwards) {
		AtomicBoolean ret = new AtomicBoolean(false);
		blockCapabilities.forEach((relativePos, dirMap) -> {
			BlockPos translatedPos = BlockPattern.translateAndRotate(frontLeftTopPos, forwards, upwards,
				relativePos.x, relativePos.y, relativePos.z);
			if (translatedPos.equals(targetPos)) {
				//DirectionRotationCache.transform(forwards, upwards, dir)
				
				var transformedDirMap = dirMap.entrySet().stream()
					.collect(Collectors.toMap(
						entry -> DirectionRotationCache.transform(forwards, upwards, entry.getKey()),
						Map.Entry::getValue
					));
				
				Set<CapabilityType> types = transformedDirMap.get(dir);
				if (types != null && types.contains(type)) {
					ret.set(true);
				}
			}
		});
		return ret.get();
	}
	
}