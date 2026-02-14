package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.client.gui.widget.base.AbstractBaseWidget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

@Setter
public abstract class AbstractScrollPanelComponent extends AbstractBaseWidget {
	protected int color;
	protected int hoverColor;
	protected int focusColor;
	private boolean hoveredOnPanel;
	protected boolean canClick = true;
	@Getter
	protected final int index;
	protected Clickable action;
	protected final Font font;
	protected final ScrollablePanel<? extends AbstractScrollPanelComponent> panel;
	public AbstractScrollPanelComponent(int x, int y, int width, int height,
			ScrollablePanel<? extends AbstractScrollPanelComponent> panel, Clickable action,
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
		double originalMy = my - getScrollAmount();
		boolean withinPanelBounds = originalMy >= panel.getY() && originalMy <= panel.getY() + panel.getHeight();
		
		hoveredOnPanel = withinPanelBounds &&
			mx >= getX() && mx <= getX() + getWidth() &&
			my >= getY() && my <= getY() + getHeight();
		return hoveredOnPanel;
	}
	
	public int getColor() {
		int color = isHovered() ? hoverColor : this.color;
		return isFocused() ? focusColor : color;
	}
	
	public void setColors(int color, int hoverColor, int focusColor) {
		this.color = color;
		this.hoverColor = hoverColor;
		this.focusColor = focusColor;
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
	public boolean isMouseOver(double mouseX, double mouseY) {
		return hoveredOnPanel;
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button) {
		if (canClick) action.onClick(mouseX, mouseY, index);
	}
	
	@Override
	public void playDownSound(SoundManager pHandler) {
		if (canClick) super.playDownSound(pHandler);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
	
	}
	
	public interface Clickable {
		void onClick(double mouseX, double mouseY, int button);
	}
}
