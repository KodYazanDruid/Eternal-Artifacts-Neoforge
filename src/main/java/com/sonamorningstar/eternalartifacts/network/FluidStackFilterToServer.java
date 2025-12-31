package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.FilterSyncable;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.network.base.PacketHelper.*;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record FluidStackFilterToServer(int containerId, int index, FluidStack fluidStack) implements ServerPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "fluid_stack_filter_to_server");
	
	public static FluidStackFilterToServer create(FriendlyByteBuf buf) {
		return new FluidStackFilterToServer(readContainerId(buf), readIndex(buf), buf.readFluidStack());
	}

	@Override
	public void write(FriendlyByteBuf buff) {
		writeContainerId(buff, containerId);
		writeIndex(buff, index);
		buff.writeFluidStack(fluidStack);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	@Override
	public void handleOnServer(ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId == containerId && menu instanceof FilterSyncable syncable) {
			syncable.fluidStackFilterSync(this);
		}
	}
}
