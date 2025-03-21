package com.sonamorningstar.eternalartifacts.client.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;

public interface Draggable {
	default AbstractWidget selfDraggable() {
		return (AbstractWidget) this;
	}
	
	
}
