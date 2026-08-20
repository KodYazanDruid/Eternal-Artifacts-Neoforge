package com.sonamorningstar.eternalartifacts.compat.emi.widgets;

import com.sonamorningstar.eternalartifacts.compat.emi.EmiEnergyStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class EtarEnergyBarWidget extends SlotWidget {
	private static final ResourceLocation BARS_TEXTURE = new ResourceLocation(MODID, "textures/gui/bars.png");
	
	private final EmiEnergyStack energyStack;
	private final int width;
	private final int height;
	private final long stored;
	private final long capacity;
	
	public EtarEnergyBarWidget(EmiEnergyStack energyStack, int x, int y, int width, int height, long capacity) {
		super(energyStack, x, y);
		this.energyStack = energyStack;
		this.width = width;
		this.height = height;
		this.stored = energyStack.getAmount();
		this.capacity = capacity;
	}
	
	@Override
	public Bounds getBounds() {
		return new Bounds(x, y, width, height);
	}
	
	@Override
	public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
		draw.blit(BARS_TEXTURE, x, y, 0, 0, 18, 56);
		int progress = (int) stored * 50 / (int) capacity;
		draw.blit(BARS_TEXTURE, x + 3, y + 53 - progress, 18,  53 - progress, 12, progress);
	}
}
