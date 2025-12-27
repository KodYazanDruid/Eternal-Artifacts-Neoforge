package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TesseractNetworkWidget extends AbstractScrollPanelComponent {
	private static final Font font = Minecraft.getInstance().font;
	private final TesseractNetwork<?> tesseractNetwork;

	public TesseractNetworkWidget(int x, int y, int width, int height, TesseractNetwork<?> tesseractNetwork,
								  ScrollablePanel<AbstractScrollPanelComponent> panel, Clickable action, int index,
								  Font font, Component message, int color, int hoverColor, int focusColor) {
		super(x, y, width, height, panel, action, index, font, message, color, hoverColor, focusColor);
		this.tesseractNetwork = tesseractNetwork;
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mX, int mY, float deltaTick) {
		int minX = getX();
		int minY = getY();
		int maxX = getX() + width;
		int maxY = getY() + height;
		int midX = (minX + maxX) / 2;
		int midY = (minY + maxY) / 2;
		int scrollInt = Mth.ceil(panel.scrollAmount());
		int margin = 3;
		super.renderWidget(gui, mX, mY, deltaTick);
		gui.fill(minX, minY, maxX, maxY, getColor());
		GuiDrawer.renderScrollingStringForPanel(gui, font, Component.literal(tesseractNetwork.getName()), minX + margin, minY + margin,
			midX - margin, midY - margin, scrollInt,0xFF38BDF8, false);
		Component capName = TesseractNetwork.CAPABILITY_NAMES.get(tesseractNetwork.getCapabilityClass());
		if (capName != null) {
			int capColor = 0xFFA78BFA;
			GuiDrawer.renderScrollingStringForPanel(gui, font, capName, minX + margin, midY + margin,
				midX - margin, maxY - margin, scrollInt, capColor, false);
		}
		GameProfile profile = tesseractNetwork.getOwner();
		if (profile != null) {
			String owner = profile.getName();
			int ownerColor = 0xFF4ADE80;
			GuiDrawer.renderScrollingStringForPanel(gui, font, Component.literal(owner), midX + margin, minY + margin,
				maxX - margin, midY - margin, scrollInt, ownerColor, false);
		}
		TesseractNetwork.Access access = tesseractNetwork.getAccess();
		if (access != null) {
			String accessName = access.name().toLowerCase();
			int accessColor = 0xFFFBBF24;
			GuiDrawer.renderScrollingStringForPanel(gui, font, Component.literal(accessName), midX + margin, midY + margin,
				maxX - margin, maxY - margin, scrollInt, accessColor, false);
		}
	}
}
