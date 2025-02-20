package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.util.ModConstants;
import lombok.Getter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DropdownMenu<W extends AbstractWidget> extends AbstractWidget {
	private static final WidgetSprites SPRITES = new WidgetSprites(
		new ResourceLocation("widget/text_field"), new ResourceLocation("widget/text_field_highlighted")
	);
	@Getter
	private ScrollablePanel<W> dropPanel;
	private final Font font;
	@Nullable
	private Component value = null;
	private final Component UNSELECTED = ModConstants.GUI.withSuffixTranslatable("dropdown_menu.unselected");
	private final List<Component> components = new ArrayList<>();
	private boolean isMenuOpen = false;
	
	public DropdownMenu(int x, int y, int width, int height, int panelHeight, /*int buttonWidth,*/ Font font) {
		super(x, y, width, height, Component.empty());
		this.font = font;
		this.dropPanel = new ScrollablePanel<>(x, y + height, width, panelHeight, 200, 10);
		dropPanel.visible = this.isMenuOpen;
		components.add(Component.literal("test1"));
		components.add(Component.literal("test2"));
		components.add(Component.literal("test3"));
		components.add(Component.literal("test4"));
		components.add(Component.literal("test5"));
		components.add(Component.literal("test6"));
		components.add(Component.literal("test7"));
		components.add(Component.literal("test8"));
		components.add(Component.literal("test9"));
		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			int finalI = i;
			dropPanel.addChild((x1, y1, width1, height1) -> {
				Button btn = Button.builder(component, b -> {
					value = component;
					isMenuOpen = false;
					dropPanel.visible = false;
				}).bounds(x1, y1 + finalI * 10, width1, 10).build();
				//btn.
				return (W) btn;
			});
		}
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mX, int mY, float deltaTick) {
		if (visible) {
			ResourceLocation tex = SPRITES.get(isActive(), isFocused());
			gui.blitSprite(tex, getX(), getY(), getWidth(), getHeight());
			gui.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
			if (value != null) {
				gui.drawString(font, value, getX() + 4, getY() + (getHeight() - 8) / 2, 0xffffff);
			} else {
				gui.drawString(font, UNSELECTED, getX() + 4, getY() + (getHeight() - 8) / 2, 0xffaaaa);
			}
			gui.disableScissor();
			if (isMenuOpen && dropPanel.visible) {
				dropPanel.render(gui, mX, mY, deltaTick);
			}
		}
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
	@Override
	public void mouseMoved(double pMouseX, double pMouseY) {
		if (isMenuOpen) {
			dropPanel.mouseMoved(pMouseX, pMouseY);
		}
		super.mouseMoved(pMouseX, pMouseY);
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
	public boolean isMouseOver(double pMouseX, double pMouseY) {
		if (isMenuOpen) {
			return isOverWithScroll(pMouseX, pMouseY) || super.isMouseOver(pMouseX, pMouseY);
		}
		return super.isMouseOver(pMouseX, pMouseY);
	}
	
	private boolean isOverWithScroll(double mx, double my) {
		return mx >= dropPanel.getX() && mx <= dropPanel.getX() + dropPanel.getWidth() + dropPanel.scrollbarWidth() &&
			my >= dropPanel.getY() && my <= dropPanel.getY() + dropPanel.getHeight();
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput narration) {
	
	}
}
