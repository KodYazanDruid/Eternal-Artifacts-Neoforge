package com.sonamorningstar.eternalartifacts.util;

import java.util.Objects;

public class RelativeBlockPos {
	public final int x;
	public final int y;
	public final int z;
	
	public RelativeBlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RelativeBlockPos that = (RelativeBlockPos) o;
		return x == that.x && y == that.y && z == that.z;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
	
	@Override
	public String toString() {
		return "RelativeBlockPos{" +
			"x=" + x +
			", y=" + y +
			", z=" + z +
			'}';
	}
}