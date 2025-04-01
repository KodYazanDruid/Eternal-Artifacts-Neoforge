package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class SimpleDraggablePanel extends AbstractWidget implements IParentalWidget, Draggable, Overlapping {
	private final List<GuiEventListener> children = new ArrayList<>();
	@Setter
	private Bounds bounds;
	private final List<Bounds> undragAreas = new ArrayList<>();
	public SimpleDraggablePanel(int pX, int pY, int pWidth, int pHeight, Bounds bounds) {
		super(pX, pY, pWidth, pHeight, Component.empty());
		this.bounds = bounds;
	}
	
	public void addUndragArea(Bounds bounds) {
		undragAreas.add(bounds);
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mx, int my, float delta) {
		gui.pose().pushPose();
		gui.pose().translate(0, 0, 360);
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		GuiDrawer.drawDefaultBackground(gui, this.getX(), this.getY(), this.width, this.height);
		renderContents(gui, mx, my, delta);
		RenderSystem.disableDepthTest();
		RenderSystem.disableBlend();
		gui.pose().popPose();
	}
	
	@Override
	protected void onDrag(double mx, double my, double dragX, double dragY) {
		for (Bounds undragArea : undragAreas) {
			if (undragArea.x() <= mx && undragArea.x() + undragArea.width() >= mx &&
				undragArea.y() <= my && undragArea.y() + undragArea.height() >= my) {
				return;
			}
		}
		int oldX = this.getX();
		int oldY = this.getY();
		int boundsX = bounds.x();
		int boundsY = bounds.y();
		int boundsWidth = bounds.width();
		int boundsHeight = bounds.height();
		int newX = (int) Math.round(Math.min(Math.max(boundsX, this.getX() + dragX), boundsX + boundsWidth - getWidth()));
		int newY = (int) Math.round(Math.min(Math.max(boundsY, this.getY() + dragY), boundsY + boundsHeight - getHeight()));
		this.setX(newX);
		this.setY(newY);
		for (GuiEventListener child : children) {
			if (child instanceof AbstractWidget widget) {
				int xOff = widget.getX() - oldX;
				int yOff = widget.getY() - oldY;
				widget.setX(newX + xOff);
				widget.setY(newY + yOff);
			}
			if (child instanceof SimpleDraggablePanel draggablePanel) {
				draggablePanel.setBounds(Bounds.of(newX, newY, getWidth(), getHeight()));
			}
		}
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		return mouseClickedChild(pMouseX, pMouseY, pButton) || super.mouseClicked(pMouseX, pMouseY, pButton);
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
	@Override
	public void mouseMoved(double pMouseX, double pMouseY) {
		super.mouseMoved(pMouseX, pMouseY);
		mouseMovedChild(pMouseX, pMouseY);
	}
	
	@Override
	public void playDownSound(SoundManager handler) {
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
	
	}
	
	public record Bounds(int x, int y, int width, int height) {
		
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
