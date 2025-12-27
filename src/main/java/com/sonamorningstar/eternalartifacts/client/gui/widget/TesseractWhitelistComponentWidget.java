package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.tesseract.TesseractNetworkRemoveWhitelistToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.UUID;

public class TesseractWhitelistComponentWidget extends AbstractScrollPanelComponent {
	private final Either<GameProfile, String> whitelisted;
	private final TesseractNetwork<?> network;
	public TesseractWhitelistComponentWidget(
			int x, int y, int width, int height,
			ScrollablePanel<? extends AbstractScrollPanelComponent> panel,
			int index, Font font, Either<GameProfile, String> whitelisted, TesseractNetwork<?> network,
			int color, int hoverColor, int focusColor) {
		super(x, y, width, height, panel, (a, b, c) -> {}, index, font,
			whitelisted.map(r -> Component.literal(r.getName()), Component::literal),
			color, hoverColor, focusColor);
		this.whitelisted = whitelisted;
		this.network = network;
		setAction((mX, mY, button) -> {
			Minecraft.getInstance().screen.setFocused(this);
		});
	}
	
	private boolean isOwner() {
		UUID id = Minecraft.getInstance().getGameProfile().getId();
		return id.equals(network.getOwner().getId());
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button) {
		if (isOwner() &&
			mouseX >= getX() + width - height && mouseX < getX() + width &&
			mouseY >= getY() && mouseY < getY() + height) {
			whitelisted.ifLeft(profile -> Channel.sendToServer(
				TesseractNetworkRemoveWhitelistToServer.create(Either.left(profile.getId()), network.getUuid())
			));
			whitelisted.ifRight(name -> Channel.sendToServer(
				TesseractNetworkRemoveWhitelistToServer.create(Either.right(name), network.getUuid())
			));
		} else super.onClick(mouseX, mouseY, button);
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mx, int my, float delta) {
		int minX = getX();
		int minY = getY() + 2;
		int maxX = getX() + width - (isOwner() ? height : 0);
		int maxY = getY() + height + 2;
		int margin = 3;
		gui.fill(RenderType.guiOverlay(), minX, minY, maxX, maxY, getColor());
		if (isOwner()) gui.fill(RenderType.guiOverlay(), maxX, minY, maxX + height, maxY, 0xFFFF6655);
		GuiDrawer.renderScrollingStringForPanel(gui, font, getMessage(), minX + margin, minY + margin,
			maxX - margin, maxY - margin, Mth.ceil(panel.scrollAmount()),0xFFFFFFFF, false);
	}
}
