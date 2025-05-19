package com.sonamorningstar.eternalartifacts.compat.emi.widgets;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TankWidget;
import net.minecraft.resources.ResourceLocation;

public class EtarTankWidget extends TankWidget {
	public EtarTankWidget(EmiIngredient stack, int x, int y, int width, int height, long capacity) {
		super(stack, x, y, width, height, capacity);
	}
	
	@Override
	public SlotWidget backgroundTexture(ResourceLocation id, int u, int v) {
		return super.backgroundTexture(id, u, v);
	}
}
