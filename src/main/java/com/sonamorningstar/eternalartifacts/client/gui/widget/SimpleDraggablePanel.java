package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
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
public class SimpleDraggablePanel extends AbstractWidget implements IParentalWidget, Draggable, Overlapping {
	private final List<GuiEventListener> children = new ArrayList<>();
	@Setter
	private Bounds bounds;
	@Setter
	private int color = 0xFFFFFFFF;
	private final List<Consumer<SimpleDraggablePanel>> onCloseListeners = new ArrayList<>();
	private final List<Bounds> undragAreas = new ArrayList<>();
	public SimpleDraggablePanel(Component title, int pX, int pY, int pWidth, int pHeight, Bounds bounds) {
		super(pX, pY, pWidth, pHeight, title);
		this.bounds = bounds;
	}
	
	public void addClosingButton() {
		addChildren((fX, fY, fW, fH) -> SpriteButton.builder(Component.empty(), (b, i) -> {
					this.visible = false;
					this.active = false;
					for (Consumer<SimpleDraggablePanel> listener : onCloseListeners) {
						listener.accept(SimpleDraggablePanel.this);
					}
				}, new ResourceLocation(MODID, "textures/gui/sprites/sided_buttons/deny.png"))
				.bounds(fX + fW - 14, fY + 5, 9, 9).build()
		);
	}
	
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
		gui.pose().translate(0, 0, 360);
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
