package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScrollablePanel<W extends AbstractWidget> extends AbstractScrollWidget {
	@Getter
	private final List<W> children = new ArrayList<>();
	@Getter
	protected int innerHeight;
	protected double scrollRate;
	public ScrollablePanel(int x, int y, int width, int height, int innerHeight, int scrollRate) {
		super(x, y, width, height, Component.empty());
		this.innerHeight = innerHeight;
		this.scrollRate = scrollRate;
	}
	
	@Override
	protected double scrollRate() {
		return scrollRate;
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
			for (W child : children) {
				if (child.mouseClicked(mx, my + scrollAmount(), button)) {
					return true;
				}
			}
		}
		return super.mouseClicked(mx, my, button);
	}
	
	@Override
	protected void renderContents(GuiGraphics gui, int mX, int mY, float deltaTick) {
		for (W child : children) {
			child.render(gui, mX, mY, deltaTick);
		}
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
