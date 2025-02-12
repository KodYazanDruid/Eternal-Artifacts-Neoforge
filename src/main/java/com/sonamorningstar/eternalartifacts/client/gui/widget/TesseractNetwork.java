package com.sonamorningstar.eternalartifacts.client.gui.widget;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.Network;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;

public class TesseractNetwork extends AbstractWidget {
	private static final Font font = Minecraft.getInstance().font;
	private final Network<?> network;
	private final IntConsumer action;
	private final int index;
	public TesseractNetwork(Network<?> network, int index, IntConsumer action, int x, int y, int width, int height) {
		super(x, y, width, height, Component.empty());
		this.network = network;
		this.index = index;
		this.action = action;
	}
	
	@Override
	protected void renderWidget(GuiGraphics gui, int mX, int mY, float deltaTick) {
		int color = isFocused() ? 0x7F31855b : 0x7F1c3e5a;
		gui.fill(getX(), getY(), getX() + width, getY() + height, color);
		gui.drawString(font, Component.literal(network.getName()), getX() + 3, getY() + 3, 0xFFFFFFFF);
		gui.drawString(font, Component.literal(network.getCapabilityClass().getName()), getX() + 3, getY() + 13, 0xFFFFFFFF);
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button) {
		if (isFocused()) action.accept(index);
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}
}
