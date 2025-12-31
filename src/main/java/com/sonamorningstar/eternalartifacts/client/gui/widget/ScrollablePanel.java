package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.client.gui.widget.base.Overlapping;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ScrollablePanel<W extends AbstractWidget> extends AbstractScrollWidget implements Overlapping {
	@Getter
	private final List<W> children = new ArrayList<>();
	@Getter
	private int innerHeight;
	protected double scrollRate;
	public ScrollablePanel(int x, int y, int width, int height, int scrollRate) {
		super(x, y, width, height, Component.empty());
		this.scrollRate = scrollRate;
	}
	
	@Override
	protected double scrollRate() {
		return scrollRate;
	}
	
	public void reCalcInnerHeight() {
		if (children.isEmpty()) {
			innerHeight = 0;
			return;
		}
		if (children.size() == 1){
			W child = children.get(0);
			innerHeight = child.getHeight();
		} else {
			AtomicInteger topY = new AtomicInteger();
			AtomicInteger bottomY = new AtomicInteger();
			children.stream().mapToInt(AbstractWidget::getY).min().ifPresentOrElse(
				topY::set,
				() -> topY.set(0)
			);
			children.stream().mapToInt(w -> w.getY() + w.getHeight()).max().ifPresentOrElse(
				bottomY::set,
				() -> bottomY.set(0)
			);
			innerHeight = bottomY.get() - topY.get();
		}
	}
	
	public void addChild(W widget) {
		children.add(widget);
	}
	public void addChild(QuadFunction<Integer, Integer, Integer, Integer, W> widgetGetter) {
		children.add(widgetGetter.apply(getX(), getY(), getWidth(), getHeight()));
	}
	public void removeChild(W widget) {
		children.remove(widget);
	}
	public void removeChild(int index) {
		children.remove(index);
	}
	public void clearChildren() {
		children.clear();
	}
	
	@Override
	public void setX(int newX) {
		int oldX = this.getX();
		for (W child : children) {
			int xOff = child.getX() - oldX;
			child.setX(newX + xOff);
		}
		super.setX(newX);
	}
	
	@Override
	public void setY(int newY) {
		int oldY = this.getY();
		for (W child : children) {
			int yOff = child.getY() - oldY;
			child.setY(newY + yOff);
		}
		super.setY(newY);
	}
	
	@Nullable
	public W getChildUnderCursor(double mouseX, double mouseY) {
		for (W child : children) {
			if (child.isMouseOver(mouseX, mouseY + scrollAmount())) {
				return child;
			}
		}
		return null;
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (visible && withinContentAreaPoint(mx, my)) {
			W child = getChildUnderCursor(mx, my);
			if (child != null) {
				return child.mouseClicked(mx, my + scrollAmount(), button);
			}
		}
		return super.mouseClicked(mx, my, button);
	}
	
	@Override
	public void mouseMoved(double mx, double my) {
		if (visible) {
			for (W child : children) {
				child.mouseMoved(mx, my);
			}
		}
		super.mouseMoved(mx, my);
	}
	
	@Override
	public boolean updateHover(double mx, double my) {
		if (visible) {
			for (W child : children) {
				if (child instanceof Overlapping overlapping) {
					overlapping.updateHover(mx, my);
				}
				if (child instanceof AbstractScrollPanelComponent aspc) {
					aspc.updateHover(mx, my);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public GuiEventListener getElementUnderMouse(double mx, double my) {
		return getChildUnderCursor(mx, my);
	}
	
	@Override
	protected void renderContents(GuiGraphics gui, int mX, int mY, float deltaTick) {
		//gui.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
		for (W child : children) {
			child.render(gui, mX, mY, deltaTick);
		}
		//gui.disableScissor();
	}
	
	@Override
	public double scrollAmount() {
		return super.scrollAmount();
	}
	
	public void setScrollAmount(double scrollAmount) {
		super.setScrollAmount(scrollAmount);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {}
}
