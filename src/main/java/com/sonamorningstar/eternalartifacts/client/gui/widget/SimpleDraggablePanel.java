package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Getter
public class SimpleDraggablePanel extends AbstractWidget implements ParentalWidget, Draggable, Overlapping {
	private final List<GuiEventListener> children = new ArrayList<>();
	@Setter
	private Bounds bounds;
	@Setter
	private int color = 0xFFFFFFFF;
	private final List<Consumer<SimpleDraggablePanel>> onCloseListeners = new ArrayList<>();
	private final List<Bounds> undragAreas = new ArrayList<>();
	private final List<Bounds> occupiedAreas = new ArrayList<>();
	public SimpleDraggablePanel(Component title, int pX, int pY, int pWidth, int pHeight, Bounds bounds) {
		super(pX, pY, pWidth, pHeight, title);
		this.bounds = bounds;
	}
	
	public void addClosingButton() {
		addChildren((fX, fY, fW, fH) -> {
			var button = SpriteButton.builder(Component.empty(), (b, i) -> {
					this.visible = false;
					this.active = false;
					for (Consumer<SimpleDraggablePanel> listener : onCloseListeners) {
						listener.accept(SimpleDraggablePanel.this);
					}
				}, new ResourceLocation(MODID, "textures/gui/sprites/sided_buttons/deny.png"))
				.bounds(fX + fW - 13, fY + 4, 9, 9).build();
			occupiedAreas.add(new Bounds(button.getX(), button.getY(), button.getWidth(), button.getHeight()));
			return button;
		});
	}
	
	public enum PlacementMode {
		AUTO,
		VERTICAL,
		HORIZONTAL,
		RELATIVE
	}
	
	public void addChildrenToUnoccupied(AbstractWidget widget, PlacementMode mode, int relativeX, int relativeY, int padding) {
		if (widget == null) return;
		
		Position position = switch (mode) {
			case VERTICAL -> findVerticalPosition(widget.getWidth(), widget.getHeight(), padding);
			case HORIZONTAL -> findHorizontalPosition(widget.getWidth(), widget.getHeight(), padding);
			case AUTO -> findAvailablePosition(widget.getWidth(), widget.getHeight(), padding);
			case RELATIVE -> findRelativePosition(relativeX, relativeY);
		};
		
		if (position != null) {
			widget.setPosition(position.x(), position.y());
			
			addChildren((fx, fy, fw, fh) -> {
				widget.setPosition(position.x(), position.y());
				return widget;
			});
			
			int cacheX = mode == PlacementMode.RELATIVE ? relativeX : position.x() - this.getX();
			int cacheY = mode == PlacementMode.RELATIVE ? relativeY : position.y() - this.getY();
			
			occupiedAreas.add(new Bounds(cacheX, cacheY, widget.getWidth(), widget.getHeight()));
		}
	}
	
	public void addChildrenToUnoccupied(AbstractWidget widget, PlacementMode mode, int padding) {
		addChildrenToUnoccupied(widget, mode, 0, 0, padding);
	}
	
	public void addChildrenToUnoccupied(AbstractWidget widget, int relativeX, int relativeY, int padding) {
		addChildrenToUnoccupied(widget, PlacementMode.RELATIVE, relativeX, relativeY, padding);
	}
	
	public void addWidgetGroup(List<WidgetPosition> widgets, int padding) {
		int leastX = 0;
		int leastY = 0;
		int totalWidth = 0;
		int totalHeight = 0;
		for (WidgetPosition wp : widgets) {
			if (wp.relativeX() < leastX) leastX = wp.relativeX();
			if (wp.relativeY() < leastY) leastY = wp.relativeY();
			if (wp.relativeX() + wp.widget().getWidth() > totalWidth) {
				totalWidth = wp.relativeX() + wp.widget().getWidth();
			}
			if (wp.relativeY() + wp.widget().getHeight() > totalHeight) {
				totalHeight = wp.relativeY() + wp.widget().getHeight();
			}
		}
		occupiedAreas.add(new Bounds(leastX, leastY, totalWidth, totalHeight));
		for (WidgetPosition wp : widgets) {
			addChildrenToUnoccupied(wp.widget(), PlacementMode.RELATIVE, wp.relativeX(), wp.relativeY(), padding);
		}
	}
	
	private Position findRelativePosition(int relativeX, int relativeY) {
		int startX = this.getX() + 4;
		int startY = this.getY() + 4;
		return new Position(startX + relativeX, startY + relativeY);
	}
	
	private Position findVerticalPosition(int widgetWidth, int widgetHeight, int padding) {
		int startX = this.getX() + 4;
		int startY = this.getY() + 4;
		int maxY = this.getY() + this.getHeight() - padding;
		
		int bottomY = startY;
		for (Bounds occupied : occupiedAreas) {
			int occupiedBottom = occupied.y() + occupied.height() + this.getY();
			if (occupiedBottom > bottomY) {
				bottomY = occupiedBottom;
			}
		}
		
		int newY = bottomY + padding;
		if (newY + widgetHeight <= maxY) {
			return new Position(startX, newY);
		}
		return null;
	}
	
	private Position findHorizontalPosition(int widgetWidth, int widgetHeight, int padding) {
		int startX = this.getX() + 4;
		int startY = this.getY() + 4;
		int maxX = this.getX() + this.getWidth() - padding;
		
		int rightX = startX;
		for (Bounds occupied : occupiedAreas) {
			int occupiedRight = occupied.x() + occupied.width() + this.getX();
			if (occupiedRight > rightX) {
				rightX = occupiedRight;
			}
		}
		
		int newX = rightX == startX ? rightX : rightX + padding;
		if (newX + widgetWidth <= maxX) {
			return new Position(newX, startY);
		}
		return null;
	}
	
	private Position findAvailablePosition(int widgetWidth, int widgetHeight, int padding) {
		int startX = this.getX() + 4;
		int startY = this.getY() + 16;
		int maxX = this.getX() + this.getWidth() - padding;
		int maxY = this.getY() + this.getHeight() - padding;
		
		for (int y = startY; y + widgetHeight <= maxY; y += padding) {
			for (int x = startX; x + widgetWidth <= maxX; x += padding) {
				Bounds testBounds = new Bounds(
					x - this.getX(),
					y - this.getY(),
					widgetWidth,
					widgetHeight
				);
				
				if (!hasOverlap(testBounds)) {
					return new Position(x, y);
				}
			}
		}
		return null;
	}
	
	private boolean hasOverlap(Bounds testBounds) {
		for (Bounds occupied : occupiedAreas) {
			if (boundsOverlap(testBounds, occupied)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean boundsOverlap(Bounds a, Bounds b) {
		return a.x() < b.x() + b.width() &&
			a.x() + a.width() > b.x() &&
			a.y() < b.y() + b.height() &&
			a.y() + a.height() > b.y();
	}
	
	public record Position(int x, int y) {}
	public record WidgetPosition(AbstractWidget widget, int relativeX, int relativeY) {}
	
	public void addOnCloseListener(Consumer<SimpleDraggablePanel> consumer) {
		onCloseListeners.add(consumer);
	}
	
	public void toggle() {
		this.visible = !this.visible;
		this.active = !this.active;
	}
	
	public void addUndragArea(Bounds bounds) {
		undragAreas.add(bounds);
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mx, int my, float delta) {
		gui.pose().pushPose();
		gui.pose().translate(0, 0, 300);
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		gui.setColor(FastColor.ARGB32.red(color) / 255.0F,
			FastColor.ARGB32.green(color) / 255.0F,
			FastColor.ARGB32.blue(color) / 255.0F,
			FastColor.ARGB32.alpha(color) / 255.0F);
		GuiDrawer.drawDefaultBackground(gui, this.getX(), this.getY(), this.width, this.height);
		renderTitle(gui, mx, my, delta);
		gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		renderContents(gui, mx, my, delta);
		RenderSystem.disableDepthTest();
		RenderSystem.disableBlend();
		gui.pose().popPose();
	}
	
	protected void renderTitle(GuiGraphics gui, int mx, int my, float delta) {
		gui.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		gui.drawString(Minecraft.getInstance().font, getMessage(), this.getX() + 8, this.getY() + 6, -1, false);
		gui.setColor(FastColor.ARGB32.red(color) / 255.0F,
			FastColor.ARGB32.green(color) / 255.0F,
			FastColor.ARGB32.blue(color) / 255.0F,
			FastColor.ARGB32.alpha(color) / 255.0F);
	}
	
	@Override
	public void setX(int newX) {
		int oldX = this.getX();
		for (GuiEventListener child : children) {
			if (child instanceof AbstractWidget widget) {
				int xOff = widget.getX() - oldX;
				widget.setX(newX + xOff);
			}
			if (child instanceof SimpleDraggablePanel draggablePanel) {
				draggablePanel.setBounds(Bounds.of(newX, this.getY(), getWidth(), getHeight()));
			}
		}
		super.setX(newX);
	}
	
	@Override
	public void setY(int newY) {
		int oldY = this.getY();
		for (GuiEventListener child : children) {
			if (child instanceof AbstractWidget widget) {
				int yOff = widget.getY() - oldY;
				widget.setY(newY + yOff);
			}
			if (child instanceof SimpleDraggablePanel draggablePanel) {
				draggablePanel.setBounds(Bounds.of(this.getX(), newY, getWidth(), getHeight()));
			}
		}
		super.setY(newY);
	}
	
	@Override
	protected void onDrag(double mx, double my, double dragX, double dragY) {
		if (!visible || !active) return;
		for (Bounds undragArea : undragAreas) {
			if (undragArea.x() <= mx && undragArea.x() + undragArea.width() >= mx &&
				undragArea.y() <= my && undragArea.y() + undragArea.height() >= my) {
				return;
			}
		}
		int boundsX = bounds.x();
		int boundsY = bounds.y();
		int boundsWidth = bounds.width();
		int boundsHeight = bounds.height();
		int newX = (int) Math.round(Math.min(Math.max(boundsX, this.getX() + dragX), boundsX + boundsWidth - getWidth()));
		int newY = (int) Math.round(Math.min(Math.max(boundsY, this.getY() + dragY), boundsY + boundsHeight - getHeight()));
		this.setX(newX);
		this.setY(newY);
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int pButton) {
		return mouseClickedChild(mx, my, pButton) || super.mouseClicked(mx, my, pButton);
	}
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		return mouseReleasedChild(pMouseX, pMouseY, pButton) || super.mouseReleased(pMouseX, pMouseY, pButton);
	}
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		return mouseDraggedChild(pMouseX, pMouseY, pButton, pDragX, pDragY) ||
			super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
		return mouseScrolledChild(pMouseX, pMouseY, pScrollX, pScrollY) ||
			super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY) {
		return isMouseOverChild(pMouseX, pMouseY) || super.isMouseOver(pMouseX, pMouseY);
	}
	public boolean isMouseOverRaw(double pMouseX, double pMouseY) {
		return super.isMouseOver(pMouseX, pMouseY);
	}
	@Override
	public void mouseMoved(double pMouseX, double pMouseY) {
		super.mouseMoved(pMouseX, pMouseY);
		mouseMovedChild(pMouseX, pMouseY);
	}
	
	@Override
	public void setFocused(boolean pFocused) {
		super.setFocused(pFocused);
	}
	
	@Override
	public void playDownSound(SoundManager handler) {
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
	
	}
	
	@Override
	public GuiEventListener getElementUnderMouse(double mx, double my) {
		return getChildUnderCursor(mx, my);
	}
	
	public record Bounds(int x, int y, int width, int height) {
		
		public static Bounds full(Screen screen) {
			return new Bounds(0, 0, screen.width, screen.height);
		}
		
		public static Bounds of(int x, int y, int width, int height) {
			return new Bounds(x, y, width, height);
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Bounds bounds = (Bounds) o;
			return x == bounds.x && y == bounds.y && width == bounds.width && height == bounds.height;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y, width, height);
		}
	}
}
