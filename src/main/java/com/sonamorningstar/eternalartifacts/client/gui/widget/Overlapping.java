package com.sonamorningstar.eternalartifacts.client.gui.widget;

public interface Overlapping {
	
	default boolean updateHover(double mx, double my) {
		return false;
	}
}
