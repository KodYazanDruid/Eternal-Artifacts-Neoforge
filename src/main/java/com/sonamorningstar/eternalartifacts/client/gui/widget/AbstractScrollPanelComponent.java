package com.sonamorningstar.eternalartifacts.client.gui.widget;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.IntConsumer;

public abstract class AbstractScrollPanelComponent extends AbstractWidget {
	@Setter
	protected int color;
	@Setter
	protected int hoverColor;
	@Setter
	protected int focusColor;
	@Setter
	private boolean hoveredOnPanel;
	@Getter
	protected final int index;
	protected final IntConsumer action;
	protected final Font font;
	protected final ScrollablePanel<? extends AbstractScrollPanelComponent> panel;
	public AbstractScrollPanelComponent(int x, int y, int width, int height,
			ScrollablePanel<? extends AbstractScrollPanelComponent> panel, IntConsumer action,
			int index, Font font, Component message, int color, int hoverColor, int focusColor
	) {
		super(x, y, width, height, message);
		this.panel = panel;
		this.color = color;
		this.hoverColor = hoverColor;
		this.focusColor = focusColor;
		this.font = font;
		this.action = action;
		this.index = index;
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mx, int my, float delta) {
	
	}
	
	public boolean updateHover(double mx, double my) {
		hoveredOnPanel = mx >= getX() && mx <= getX() + getWidth() &&
			my >= Math.max(getY() - getScrollInt(), panel.getY()) &&
			my <= Math.min(getY() - getScrollInt() + getHeight(), panel.getY() + panel.getHeight());
		return hoveredOnPanel;
	}
	
	public int getColor() {
		int color = isHovered() ? hoverColor : this.color;
		return isFocused() ? focusColor : color;
	}
	
	public int getScrollInt() {
		return Mth.ceil(getScrollAmount());
	}
	
	public double getScrollAmount() {
		return panel.scrollAmount();
	}
	
	@Override
	public boolean isHovered() {
		return hoveredOnPanel;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button) {
		action.accept(index);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
	
	}
}
