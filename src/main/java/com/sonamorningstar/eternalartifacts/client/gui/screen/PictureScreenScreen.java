package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.widget.CleanButton;
import com.sonamorningstar.eternalartifacts.container.PictureScreenMenu;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.SendStringToServer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PictureScreenScreen extends AbstractModContainerScreen<PictureScreenMenu> {
	private EditBox linkInput;
	public PictureScreenScreen(PictureScreenMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		setImageSize(200, 80);
	}
	
	@Override
	protected void init() {
		super.init();
		linkInput = new EditBox(this.font, this.leftPos + 10, this.topPos + 20, this.imageWidth - 20, 20, Component.translatable("eternalartifacts.gui.picture_screen.link"));
		linkInput.setMaxLength(256);
		linkInput.setValue(menu.screen.getImageUrl());
		addRenderableWidget(linkInput);
		addRenderableWidget(CleanButton.builder(Component.translatable("eternalartifacts.gui.picture_screen.set_link"), button -> {
			menu.screen.setImageUrl(linkInput.getValue());
			Channel.sendToServer(new SendStringToServer(menu.containerId, linkInput.getValue()));
		}).bounds(this.leftPos + 10, this.topPos + 50, this.imageWidth - 20, 20).build());
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
	}
}
