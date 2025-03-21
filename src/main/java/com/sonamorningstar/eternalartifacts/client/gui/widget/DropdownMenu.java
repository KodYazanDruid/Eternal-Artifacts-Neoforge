package com.sonamorningstar.eternalartifacts.client.gui.widget;

import lombok.Getter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Getter
public class DropdownMenu<W extends AbstractWidget> extends AbstractWidget implements Overlapping {
	private static final WidgetSprites SPRITES = new WidgetSprites(
		new ResourceLocation("widget/text_field"), new ResourceLocation("widget/text_field_highlighted")
	);
	private final ScrollablePanel<W> dropPanel;
	private final Font font;
	@Nullable
	private W value = null;
	private int index = 0;
	private final Component unselectedText;
	private boolean isMenuOpen = false;
	
	public DropdownMenu(int x, int y, int width, int height, int panelHeight,
					Font font, Consumer<PanelBuilder<W>> panelBuilder, Component unselectedText) {
		super(x, y, width, height, Component.empty());
		this.font = font;
		this.unselectedText = unselectedText;
		this.dropPanel = new ScrollablePanel<>(x, y + height, width,panelHeight, 10);
		dropPanel.visible = this.isMenuOpen;
		panelBuilder.accept(getPanelBuilder());
		dropPanel.reCalcInnerHeight();
	}
	
	public void select(int index) {
		value = dropPanel.getChildren().get(index);
		this.index = index;
		closeMenu();
	}
	
	public void closeMenu() {
		isMenuOpen = false;
		dropPanel.visible = false;
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mX, int mY, float deltaTick) {
		if (visible) {
			ResourceLocation tex = SPRITES.get(isActive(), isFocused());
			gui.pose().translate(0, 0, 1);
			gui.blitSprite(tex, getX(), getY(), getFieldWidth(), getFieldHeight());
			gui.enableScissor(getX(), getY(), getX() + getFieldWidth(), getY() + getFieldHeight());
			gui.pose().translate(0, 0, 1);
			if (value != null) {
				gui.drawString(font, value.getMessage(), getX() + 4, getY() + (getFieldHeight() - 8) / 2, 0xffffff);
			} else {
				gui.drawString(font, Component.literal("<").append(unselectedText).append(">"), getX() + 4, getY() + (getFieldHeight() - 8) / 2, 0xffaaaa);
			}
			gui.disableScissor();
			if (isMenuOpen) {
				dropPanel.render(gui, mX, mY, deltaTick);
			}
		}
	}
	
	public int getFieldWidth() {
		return super.getWidth();
	}
	
	public int getFieldHeight() {
		return super.getHeight();
	}
	
	@Override
	public int getWidth() {
		return super.getWidth() + (isMenuOpen ? dropPanel.getWidth() : 0);
	}
	
	@Override
	public int getHeight() {
		return super.getHeight() + (isMenuOpen ? dropPanel.getHeight() : 0);
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (isMenuOpen){
			return dropPanel.mouseClicked(mx, my, button) || super.mouseClicked(mx, my, button);
		}
		return super.mouseClicked(mx, my, button);
	}
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		if (isMenuOpen) {
			return dropPanel.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
		if (isMenuOpen) {
			return dropPanel.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY) ||
				super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
		}
		return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
	}
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		if (isMenuOpen) {
			return dropPanel.mouseReleased(pMouseX, pMouseY, pButton) ||
				super.mouseReleased(pMouseX, pMouseY, pButton);
		}
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
	public boolean mouseMoveConsumer(double mx, double my) {
		if (isMenuOpen) {
			dropPanel.mouseMoved(mx, my);
			return true;
		}
		super.mouseMoved(mx, my);
		return false;
	}
	
	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (isMenuOpen) {
			dropPanel.setFocused(focused);
		}
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (isMenuOpen) {
			return dropPanel.keyPressed(pKeyCode, pScanCode, pModifiers) ||
				super.keyPressed(pKeyCode, pScanCode, pModifiers);
		}
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button) {
		isMenuOpen = !isMenuOpen;
		dropPanel.visible = isMenuOpen;
	}
	
	@Override
	public boolean updateHover(double mx, double my) {
		if (isMenuOpen) {
			return dropPanel.updateHover(mx, my);
		}
		return false;
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY) {
		if (isMenuOpen) {
			return isOverWithScroll(pMouseX, pMouseY) || super.isMouseOver(pMouseX, pMouseY);
		}
		return super.isMouseOver(pMouseX, pMouseY);
	}
	
	private boolean isOverWithScroll(double mx, double my) {
		boolean panelOver = mx >= dropPanel.getX() && mx <= dropPanel.getX() + dropPanel.getWidth() &&
			my >= dropPanel.getY() && my <= dropPanel.getY() + dropPanel.getHeight();
		boolean barOver = mx >= dropPanel.getX() + dropPanel.getWidth() &&
			mx <= dropPanel.getX() + dropPanel.getWidth() + dropPanel.scrollbarWidth() &&
			my >= dropPanel.getY() + dropPanel.scrollAmount() &&
			my <= dropPanel.getY() + dropPanel.getHeight() + dropPanel.scrollAmount();
		return panelOver || barOver;
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narration) {
	
	}
	
	public PanelBuilder<W> getPanelBuilder() {
		return new PanelBuilder<>(this);
	}
	
	public record PanelBuilder<W extends AbstractWidget>(DropdownMenu<W> menu) {
	
		public W add(W widget) {
			menu.dropPanel.addChild(widget);
			return widget;
		}
	}
}
