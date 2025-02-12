package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScrollablePanel extends AbstractScrollWidget {
	@Getter
	private final List<AbstractWidget> children = new ArrayList<>();
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
	
	public void addChild(AbstractWidget widget) {
		children.add(widget);
	}
	public void addChild(QuadFunction<Integer, Integer, Integer, Integer, AbstractWidget> widgetGetter) {
		children.add(widgetGetter.apply(getX(), getY(), getWidth(), getHeight()));
	}
	public void removeChild(AbstractWidget widget) {
		children.remove(widget);
	}
	public void removeChild(int index) {
		children.remove(index);
	}
	public void clearChildren() {
		children.clear();
	}
	
	@Nullable
	public AbstractWidget getChildUnderCursor(double mouseX, double mouseY) {
		for (AbstractWidget child : children) {
			if (child.isMouseOver(mouseX, mouseY + scrollAmount())) {
				return child;
			}
		}
		return null;
	}
	
	@Override
	protected void renderContents(GuiGraphics gui, int mX, int mY, float deltaTick) {
		for (AbstractWidget child : children) {
			child.render(gui, mX, mY, deltaTick);
		}
	}
	
	@Override
	public double scrollAmount() {
		return super.scrollAmount();
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {}
}
