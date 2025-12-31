package com.sonamorningstar.eternalartifacts.client.gui.widget.base;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.SimpleDraggablePanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractBaseWidget extends AbstractWidget {
	public AbstractBaseWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
		super(pX, pY, pWidth, pHeight, pMessage);
	}
	
	@Override
	public boolean isMouseOver(double mX, double mY) {
		if (this instanceof ParentalWidget) return super.isMouseOver(mX, mY);
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof AbstractModContainerScreen<?> modScreen) {
			for (int i = modScreen.upperLayerChildren.size() - 1; i >= 0; i--) {
				GuiEventListener child = modScreen.upperLayerChildren.get(i);
				if (child instanceof ParentalWidget parental) {
					if (child instanceof SimpleDraggablePanel panel && panel.isMouseOverRaw(mX, mY)) {
						return parental.getChildUnderCursorRaw(mX, mY) == this;
					}
				}
			}
		}
		return super.isMouseOver(mX, mY);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible && this.isValidClickButton(button)) {
			if (this.clicked(mouseX, mouseY)) {
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				this.onClick(mouseX, mouseY, button);
				return true;
			}
		}
		return false;
	}
	
}
