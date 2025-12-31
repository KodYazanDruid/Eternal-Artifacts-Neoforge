package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.client.gui.widget.base.AbstractBaseWidget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.DoubleConsumer;

public class ScrollBarWidget extends AbstractBaseWidget implements Renderable, GuiEventListener {
	private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("widget/scroller");
	private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("widget/text_field");
	
	@Getter
	private double scrollAmount;
	private boolean scrolling;
	private int contentHeight;
	@Setter
	private double scrollRate = 10.0;
	@Setter
	private DoubleConsumer onScrolled;
	
	public ScrollBarWidget(int x, int y, int width, int height, int contentHeight) {
		super(x, y, width, height, Component.empty());
		this.contentHeight = contentHeight;
	}
	
	public void setContentHeight(int contentHeight) {
		this.contentHeight = contentHeight;
		this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0, this.getMaxScrollAmount());
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!this.visible || !this.active) {
			return false;
		}
		
		boolean insideScrollbar = mouseX >= this.getX() && mouseX <= this.getX() + this.width &&
			mouseY >= this.getY() && mouseY <= this.getY() + this.height;
		
		if (insideScrollbar && button == 0) {
			this.scrolling = true;
			
			if (mouseY < getThumbY()) {
				setScrollAmount(Math.max(0, this.scrollAmount - this.height));
			} else if (mouseY > getThumbY() + getThumbHeight()) {
				setScrollAmount(Math.min(this.getMaxScrollAmount(), this.scrollAmount + this.height));
			}
			
			notifyScrollChanged();
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.scrolling = false;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (this.visible && this.active && this.scrolling) {
			int thumbHeight = getThumbHeight();
			double factor = Math.max(1, this.getMaxScrollAmount() / (double)(this.height - thumbHeight));
			this.scrollAmount = Mth.clamp(this.scrollAmount + dragY * factor, 0.0, this.getMaxScrollAmount());
			notifyScrollChanged();
			return true;
		}
		return false;
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narr) {
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (!this.visible || !this.active) {
			return false;
		}
		
		/*boolean insideScrollbar = mouseX >= this.getX() && mouseX <= this.getX() + this.width &&
			mouseY >= this.getY() && mouseY <= this.getY() + this.height;*/
		
		//if (insideScrollbar) {
			this.scrollAmount = Mth.clamp(this.scrollAmount - scrollY * this.scrollRate, 0.0, this.getMaxScrollAmount());
			notifyScrollChanged();
			return true;
		//}
		
		//return false;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		boolean isUp = keyCode == 265;
		boolean isDown = keyCode == 264;
		
		if ((isUp || isDown) && this.active) {
			double oldScroll = this.scrollAmount;
			this.scrollAmount = Mth.clamp(this.scrollAmount + (isUp ? -1 : 1) * this.scrollRate, 0.0, this.getMaxScrollAmount());
			
			if (oldScroll != this.scrollAmount) {
				notifyScrollChanged();
				return true;
			}
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		if (!this.visible) {
			return;
		}
		
		graphics.blitSprite(BACKGROUND_SPRITE, this.getX(), this.getY(), this.width, this.height);
		
		if (shouldShowScrollBar()) {
			int thumbY = getThumbY();
			int thumbHeight = getThumbHeight();
			graphics.blitSprite(SCROLLER_SPRITE, this.getX(), thumbY, this.width, thumbHeight);
		}
	}
	
	private int getThumbY() {
		return this.getY() + (int)(this.scrollAmount * (this.height - getThumbHeight()) / this.getMaxScrollAmount());
	}
	
	private int getThumbHeight() {
		return Mth.clamp((int)((float)(this.height * this.height) / (float)this.contentHeight), 32, this.height);
	}
	
	private boolean shouldShowScrollBar() {
		return this.contentHeight > this.height;
	}
	
	private double getMaxScrollAmount() {
		return Math.max(0, this.contentHeight - this.height);
	}
	
	public double getScrollPercentage() {
		return this.getMaxScrollAmount() > 0 ? this.scrollAmount / this.getMaxScrollAmount() : 0.0;
	}
	
	public void setScrollAmount(double scrollAmount) {
		double oldScroll = this.scrollAmount;
		this.scrollAmount = Mth.clamp(scrollAmount, 0.0, this.getMaxScrollAmount());
		
		if (oldScroll != this.scrollAmount) {
			notifyScrollChanged();
		}
	}
	
	private void notifyScrollChanged() {
		if (this.onScrolled != null) {
			this.onScrolled.accept(this.scrollAmount);
		}
	}
}