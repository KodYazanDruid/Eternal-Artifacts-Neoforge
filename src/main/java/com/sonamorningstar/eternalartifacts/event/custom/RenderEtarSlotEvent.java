package com.sonamorningstar.eternalartifacts.event.custom;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import lombok.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class RenderEtarSlotEvent extends Event implements ICancellableEvent {
	private final AbstractModContainerScreen<?> screen;
	private final GuiGraphics gui;
	private final Slot slot;
	private final ResourceLocation texture;
	private final int x;
	private final int y;
	private final int blitOffset;
	private final int width;
	private final int height;
	
	private int guiTint = 0xFFFFFFFF;
}
