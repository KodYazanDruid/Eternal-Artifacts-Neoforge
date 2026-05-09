package com.sonamorningstar.eternalartifacts.util;

public record RelativeBlockPos(int x, int y, int z) {
	
	@Override
	public String toString() {
		return "RelativeBlockPos{" +
			"x=" + x +
			", y=" + y +
			", z=" + z +
			'}';
	}
}