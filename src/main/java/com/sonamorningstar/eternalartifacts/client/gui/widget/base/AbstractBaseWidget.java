package com.sonamorningstar.eternalartifacts.client.gui.widget.base;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractBaseWidget extends AbstractWidget {
	public AbstractBaseWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
		super(pX, pY, pWidth, pHeight, pMessage);
	}
	
	/**
	 * Raw bounds check without any Overlapping widget checks.
	 * Use this to avoid recursive calls.
	 */
	public boolean isMouseOverRaw(double mX, double mY) {
		return this.active && this.visible &&
			mX >= this.getX() && mX < this.getX() + this.getWidth() &&
			mY >= this.getY() && mY < this.getY() + this.getHeight();
	}
	
	@Override
	public boolean isMouseOver(double mX, double mY) {
		if (this instanceof ParentalWidget) return super.isMouseOver(mX, mY);
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof AbstractModContainerScreen<?> modScreen) {
			for (int i = modScreen.upperLayerChildren.size() - 1; i >= 0; i--) {
				GuiEventListener child = modScreen.upperLayerChildren.get(i);
				if (child == this) continue;
				if (child instanceof Overlapping overlapping && child instanceof AbstractWidget widget) {
					boolean isOver = (widget instanceof AbstractBaseWidget abw)
						? abw.isMouseOverRaw(mX, mY)
						: widget.isMouseOver(mX, mY);
					if (isOver) {
						GuiEventListener elementUnder = overlapping.getElementUnderMouse(mX, mY);
						return elementUnder == this;
					}
				}
			}
			for (GuiEventListener child : modScreen.children()) {
				if (child == this) continue;
				if (child instanceof Overlapping overlapping && child instanceof AbstractWidget widget) {
					boolean isOver = (widget instanceof AbstractBaseWidget abw)
						? abw.isMouseOverRaw(mX, mY)
						: widget.isMouseOver(mX, mY);
					if (isOver) {
						GuiEventListener elementUnder = overlapping.getElementUnderMouse(mX, mY);
						return elementUnder == this;
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
