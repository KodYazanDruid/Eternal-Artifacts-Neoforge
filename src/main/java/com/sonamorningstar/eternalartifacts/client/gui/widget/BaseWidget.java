package com.sonamorningstar.eternalartifacts.client.gui.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class BaseWidget extends AbstractWidget {
	public BaseWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
		super(pX, pY, pWidth, pHeight, pMessage);
	}
	
}
