package com.sonamorningstar.eternalartifacts.compat.emi;

import com.sonamorningstar.eternalartifacts.util.ModConstants;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class EmiEnergyStack extends EmiStack {
	
	private static final class Energy { }
	public static final Energy TYPE = new Energy();
	
	public EmiEnergyStack(long amount) {
		this.amount = amount;
	}
	@Override
	public EmiStack copy() {
		EmiEnergyStack e = new EmiEnergyStack(this.amount);
		e.setChance(this.getChance());
		e.comparison = this.comparison;
		return e;
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int i, int i1, float v, int i2) {}
	
	@Override
	public boolean isEmpty() {
		return amount <= 0;
	}
	
	@Override
	public CompoundTag getNbt() {
		return null;
	}
	
	@Override
	public Object getKey() {
		return TYPE;
	}
	
	@Override
	public ResourceLocation getId() {
		return new ResourceLocation(MODID, "energy");
	}
	
	@Override
	public List<ClientTooltipComponent> getTooltip() {
		List<ClientTooltipComponent> tooltips = new ArrayList<>();
		for (Component component : getTooltipText()) {
			tooltips.add(ClientTooltipComponent.create(component.getVisualOrderText()));
		}
		return tooltips;
	}
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###");
	@Override
	public List<Component> getTooltipText() {
		List<Component> tooltips = new ArrayList<>();
		tooltips.add(Component.literal(DECIMAL_FORMAT.format(amount) + " RF"));
		return tooltips;
	}
	
	@Override
	public Component getName() {
		return ModConstants.ENERGY_CAPABILITY.translatable();
	}
}
