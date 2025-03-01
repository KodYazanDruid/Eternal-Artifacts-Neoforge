package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntConsumer;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ScrollablePanelComponent extends AbstractScrollPanelComponent {
	
	public ScrollablePanelComponent(int x, int y, int width, int height,
				ScrollablePanel<? extends ScrollablePanelComponent> panel, IntConsumer action,
				int index, Font font, Component message, int color, int hoverColor, int focusColor) {
		super(x, y, width, height, panel, action, index, font, message, color, hoverColor, focusColor);
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mx, int my, float delta) {
		super.renderWidget(gui, mx, my, delta);
		gui.fill(getX(), getY(), getX() + width, getY() + height, getColor());
		gui.blitSprite(new ResourceLocation(MODID, "right_arrow"), getX() + 2, getY() + (getHeight() - 8) / 2, 16, 8);
		GuiDrawer.renderScrollingStringForPanel(gui, font, getMessage(),getX() + 20, getY(),
			getX() + getWidth() - 2, getY() + getHeight(),
			getScrollInt(), 0xffffffff, false);
	}
}
