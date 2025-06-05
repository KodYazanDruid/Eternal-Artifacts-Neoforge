package com.sonamorningstar.eternalartifacts.client.gui.widget;

import net.minecraft.client.gui.components.events.GuiEventListener;

public interface Overlapping {
	
	default boolean updateHover(double mx, double my) {
		return false;
	}
	
	GuiEventListener getElementUnderMouse(double mx, double my);
}
