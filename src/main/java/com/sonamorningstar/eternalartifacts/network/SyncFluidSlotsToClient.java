package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.network.base.ClientPayload;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record SyncFluidSlotsToClient(NonNullList<FluidStack> fluids) implements ClientPayload {
	
	public static final ResourceLocation ID = new ResourceLocation(MODID, "sync_fluid_slots_to_client");
	
	public static SyncFluidSlotsToClient create(FriendlyByteBuf buffer) {
		return new SyncFluidSlotsToClient(buffer.readCollection(NonNullList::createWithCapacity, FluidStack::readFromPacket));
	}
	
	@Override
	public void handleOnClient(Minecraft minecraft) {
		Screen screen = minecraft.screen;
		if (screen instanceof AbstractModContainerScreen<?> amcs) {
			AbstractModContainerMenu menu = amcs.getMenu();
			for (int i = 0; i < fluids.size(); i++) {
				menu.getFluidSlot(i).setFluid(fluids.get(i));
			}
		}
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeCollection(fluids, (buf, stack) -> stack.writeToPacket(buf));
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
}
