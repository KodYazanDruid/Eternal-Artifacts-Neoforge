package com.sonamorningstar.eternalartifacts.client.render.util;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class DirectionRotationCache {
	
	private static final Map<Key, Map<Direction, Direction>> CACHE = buildCache();
	
	private static Map<Key, Map<Direction, Direction>> buildCache() {
		Map<Key, Map<Direction, Direction>> map = new java.util.HashMap<>();
		
		for (Direction forward : Direction.values()) {
			for (Direction up : Direction.values()) {
				if (up == forward || up == forward.getOpposite()) continue;
				
				Key key = new Key(forward, up);
				
				Map<Direction, Direction> transform = new EnumMap<>(Direction.class);
				
				Direction right = getRight(forward, up);
				Direction down = up.getOpposite();
				Direction back = forward.getOpposite();
				
				transform.put(Direction.UP, up);
				transform.put(Direction.DOWN, down);
				transform.put(Direction.NORTH, forward);
				transform.put(Direction.SOUTH, back);
				transform.put(Direction.EAST, right);
				transform.put(Direction.WEST, right.getOpposite());
				
				map.put(key, Map.copyOf(transform));
			}
		}
		
		return Map.copyOf(map);
	}
	
	public static Direction transform(Direction forward, Direction up, Direction dir) {
		var map = CACHE.get(new Key(forward, up));
		if (map == null) return null;
		return CACHE.get(new Key(forward, up)).get(dir);
	}
	
	private static Direction getRight(Direction forward, Direction up) {
		for (Direction dir : Direction.values()) {
			if (dir != forward && dir != up && dir != forward.getOpposite() && dir != up.getOpposite()) {
				if (up == cross(forward, dir)) {
					return dir;
				}
			}
		}
		throw new IllegalStateException("No right found for " + forward + " " + up);
	}
	
	private static Direction cross(Direction a, Direction b) {
		int ax = a.getStepX(), ay = a.getStepY(), az = a.getStepZ();
		int bx = b.getStepX(), by = b.getStepY(), bz = b.getStepZ();
		
		int cx = ay * bz - az * by;
		int cy = az * bx - ax * bz;
		int cz = ax * by - ay * bx;
		
		for (Direction dir : Direction.values()) {
			if (dir.getStepX() == cx && dir.getStepY() == cy && dir.getStepZ() == cz) {
				return dir;
			}
		}
		throw new IllegalStateException("Invalid cross " + a + " × " + b);
	}
	
	private record Key(Direction forward, Direction up) {}
}
