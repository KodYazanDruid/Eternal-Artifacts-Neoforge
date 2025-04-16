package com.sonamorningstar.eternalartifacts.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import javax.annotation.Nullable;
import java.util.List;

public interface IParentalWidget {
	List<GuiEventListener> getChildren();
	default AbstractWidget self() {
		return (AbstractWidget) this;
	}
	
	default void addChildren(ChildAdder adder) {
		getChildren().add(adder.addChild(self().getX(), self().getY(), self().getWidth(), self().getHeight()));
	}
	
	@Nullable
	default GuiEventListener getChildUnderCursor(double mouseX, double mouseY) {
		if (!self().isActive() || !self().visible) return null;
		List<GuiEventListener> children = getChildren();
		for (int i = children.size() - 1; i >= 0; i--) {
			GuiEventListener child = children.get(i);
			if (child.isMouseOver(mouseX, mouseY)) {
				return child;
			}
		}
		return null;
	}
	
	default boolean mouseClickedChild(double mx, double my, int button) {
		if (self().isMouseOver(mx,my) ) {
			GuiEventListener child = getChildUnderCursor(mx, my);
			if (child != null) {
				return child.mouseClicked(mx, my, button);
			}
		}
		return false;
	}
	default boolean mouseReleasedChild(double mx, double my, int button) {
		if (self().isMouseOver(mx,my) ) {
			GuiEventListener child = getChildUnderCursor(mx, my);
			if (child != null) {
				return child.mouseReleased(mx, my, button);
			}
		}
		return false;
	}
	default boolean mouseDraggedChild(double mx, double my, int button, double dx, double dy) {
		if (self().isMouseOver(mx,my) ) {
			GuiEventListener child = getChildUnderCursor(mx, my);
			if (child != null) {
				return child.mouseDragged(mx, my, button, dx, dy);
			}
		}
		return false;
	}
	default boolean mouseScrolledChild(double mx, double my, double scrollX, double scrollY) {
		if (self().isMouseOver(mx,my) ) {
			GuiEventListener child = getChildUnderCursor(mx, my);
			if (child != null) {
				return child.mouseScrolled(mx, my, scrollX, scrollY);
			}
		}
		return false;
	}
	default boolean isMouseOverChild(double mx, double my) {
		return getChildUnderCursor(mx, my) != null;
	}
	default void mouseMovedChild(double mx, double my) {
		if (self().visible) {
			for (GuiEventListener child : getChildren()) {
				child.mouseMoved(mx, my);
			}
		}
	}
	
	default void renderContents(GuiGraphics gui, int mX, int mY, float deltaTick) {
		for (GuiEventListener child : getChildren()) {
			if (child instanceof Renderable renderable) renderable.render(gui, mX, mY, deltaTick);
		}
	}
	
	interface ChildAdder {
		GuiEventListener addChild(int x, int y, int width, int height);
	}
}
