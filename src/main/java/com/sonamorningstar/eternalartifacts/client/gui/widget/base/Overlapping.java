package com.sonamorningstar.eternalartifacts.client.gui.widget.base;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;

import javax.annotation.Nullable;

/**
 * Interface for widgets that can overlap with other UI elements.
 * Provides methods for managing hover states and detecting elements under the mouse cursor.
 */
public interface Overlapping {
	
	/**
	 * Updates the hover state for this widget and its children.
	 * @param mx mouse x coordinate
	 * @param my mouse y coordinate
	 * @param isBlocked whether this widget is blocked by another overlapping widget above it
	 * @return true if this widget or any of its children is being hovered
	 */
	default boolean updateHover(double mx, double my, boolean isBlocked) {
		return false;
	}
	
	/**
	 * Returns the GUI element currently under the mouse cursor.
	 * @param mx mouse x coordinate
	 * @param my mouse y coordinate
	 * @return the element under the cursor, or null if none
	 */
	@Nullable
	GuiEventListener getElementUnderMouse(double mx, double my);
	
	/**
	 * Checks if the mouse is over this widget's own bounds (excluding children).
	 * @param mx mouse x coordinate
	 * @param my mouse y coordinate
	 * @return true if the mouse is within this widget's bounds
	 */
	default boolean isMouseOverSelf(double mx, double my) {
		if (this instanceof AbstractWidget widget) {
			return widget.visible && widget.active &&
				mx >= widget.getX() && mx < widget.getX() + widget.getWidth() &&
				my >= widget.getY() && my < widget.getY() + widget.getHeight();
		}
		return false;
	}
	
	/**
	 * Clears all hover states for this widget and its children.
	 */
	default void clearHover() {
		updateHover(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true);
	}
}
