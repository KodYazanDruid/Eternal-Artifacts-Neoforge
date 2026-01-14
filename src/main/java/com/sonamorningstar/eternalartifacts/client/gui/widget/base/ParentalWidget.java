package com.sonamorningstar.eternalartifacts.client.gui.widget.base;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SlotWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import javax.annotation.Nullable;
import java.util.List;

public interface ParentalWidget {
	List<GuiEventListener> getChildren();
	default AbstractWidget self() {
		return (AbstractWidget) this;
	}
	
	/**
	 * Raw bounds check for the parent widget itself (no recursive isMouseOver calls)
	 */
	default boolean isMouseOverSelfRaw(double mx, double my) {
		AbstractWidget self = self();
		return self.active && self.visible &&
			mx >= self.getX() && mx < self.getX() + self.getWidth() &&
			my >= self.getY() && my < self.getY() + self.getHeight();
	}
	
	/**
	 * Raw bounds check for a child widget (no recursive isMouseOver calls)
	 */
	default boolean isChildMouseOverRaw(GuiEventListener child, double mx, double my) {
		if (child instanceof AbstractWidget widget) {
			return widget.active && widget.visible &&
				mx >= widget.getX() && mx < widget.getX() + widget.getWidth() &&
				my >= widget.getY() && my < widget.getY() + widget.getHeight();
		}
		return child.isMouseOver(mx, my);
	}
	
	default void addChildren(ChildAdder adder) {
		GuiEventListener child = adder.addChild(self().getX(), self().getY(), self().getWidth(), self().getHeight());
		getChildren().add(child);
		
		if (child instanceof SlotWidget slotWidget) {
			if (Minecraft.getInstance().screen instanceof AbstractModContainerScreen<?> screen) {
				slotWidget.registerToScreen(screen);
			}
		}
	}
	
	@Nullable
	default GuiEventListener getChildUnderCursor(double mouseX, double mouseY) {
		if (!self().isActive() || !self().visible) return null;
		List<GuiEventListener> children = getChildren();
		for (int i = children.size() - 1; i >= 0; i--) {
			GuiEventListener child = children.get(i);
			// Raw bounds check kullanarak recursive çağrıyı önle
			if (isChildMouseOverRaw(child, mouseX, mouseY)) {
				return child;
			}
		}
		return null;
	}
	
	@Nullable
	default GuiEventListener getChildUnderCursorRaw(double mouseX, double mouseY) {
		if (!self().isActive() || !self().visible) return null;
		List<GuiEventListener> children = getChildren();
		for (int i = children.size() - 1; i >= 0; i--) {
			GuiEventListener child = children.get(i);
			if (child instanceof AbstractWidget widget) {
				if (widget.getX() <= mouseX && widget.getX() + widget.getWidth() >= mouseX &&
					widget.getY() <= mouseY && widget.getY() + widget.getHeight() >= mouseY) {
					return child;
				}
			}
		}
		return null;
	}
	
	default boolean mouseClickedChild(double mx, double my, int button) {
		// Raw bounds check kullanarak recursive çağrıyı önle
		if (isMouseOverSelfRaw(mx, my)) {
			GuiEventListener child = getChildUnderCursor(mx, my);
			if (child != null) {
				return child.mouseClicked(mx, my, button);
			}
		}
		return false;
	}
	default boolean mouseReleasedChild(double mx, double my, int button) {
		if (isMouseOverSelfRaw(mx, my)) {
			GuiEventListener child = getChildUnderCursor(mx, my);
			if (child != null) {
				return child.mouseReleased(mx, my, button);
			}
		}
		return false;
	}
	default boolean mouseDraggedChild(double mx, double my, int button, double dx, double dy) {
		if (isMouseOverSelfRaw(mx, my)) {
			GuiEventListener child = getChildUnderCursor(mx, my);
			if (child != null) {
				return child.mouseDragged(mx, my, button, dx, dy);
			}
		}
		return false;
	}
	default boolean mouseScrolledChild(double mx, double my, double scrollX, double scrollY) {
		if (isMouseOverSelfRaw(mx, my)) {
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
			if (child instanceof Renderable renderable) {
				renderable.render(gui, mX, mY, deltaTick);
			}
		}
	}
	
	interface ChildAdder {
		GuiEventListener addChild(int x, int y, int width, int height);
	}
}
