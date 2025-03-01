package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.Network;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.IntConsumer;

public class TesseractNetwork extends AbstractScrollPanelComponent {
	private static final Font font = Minecraft.getInstance().font;
	private final Network<?> network;

	public TesseractNetwork(int x, int y, int width, int height, Network<?> network,
							ScrollablePanel<TesseractNetwork> panel, IntConsumer action, int index,
							Font font, Component message, int color, int hoverColor, int focusColor) {
		super(x, y, width, height, panel, action, index, font, message, color, hoverColor, focusColor);
		this.network = network;
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
		GuiDrawer.renderScrollingStringForPanel(gui, font, Component.literal(network.getName()), minX + margin, minY + margin,
			midX - margin, midY - margin, scrollInt,0xFF38BDF8, false);
		Component capName = Network.CAPABILITY_NAMES.get(network.getCapabilityClass());
		if (capName != null) {
			int capColor = 0xFFA78BFA;
			GuiDrawer.renderScrollingStringForPanel(gui, font, capName, minX + margin, midY + margin,
				midX - margin, maxY - margin, scrollInt, capColor, false);
		}
		GameProfile profile = network.getOwner();
		if (profile != null) {
			String owner = profile.getName();
			int ownerColor = 0xFF4ADE80;
			GuiDrawer.renderScrollingStringForPanel(gui, font, Component.literal(owner), midX + margin, minY + margin,
				maxX - margin, midY - margin, scrollInt, ownerColor, false);
		}
		Network.Access access = network.getAccess();
		if (access != null) {
			String accessName = access.name().toLowerCase();
			int accessColor = 0xFFFBBF24;
			GuiDrawer.renderScrollingStringForPanel(gui, font, Component.literal(accessName), midX + margin, midY + margin,
				maxX - margin, maxY - margin, scrollInt, accessColor, false);
		}
	}
	
}
