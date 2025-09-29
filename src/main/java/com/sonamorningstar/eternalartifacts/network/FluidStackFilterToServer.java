package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.AbstractPipeFilterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record FluidStackFilterToServer(int containerId, int index, FluidStack fluidStack) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "fluid_stack_filter_to_server");
	
	public static FluidStackFilterToServer create(FriendlyByteBuf buf) {
		return new FluidStackFilterToServer(buf.readByte(), buf.readVarInt(), buf.readFluidStack());
	}
	
	public static FluidStackFilterToServer create(int id, int index, FluidStack fluidStack) {
		return new FluidStackFilterToServer(id, index, fluidStack);
	}

	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeByte(containerId);
		buff.writeVarInt(index);
		buff.writeFluidStack(fluidStack);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
			AbstractContainerMenu menu = player.containerMenu;
			if (menu.containerId == containerId && menu instanceof AbstractPipeFilterMenu modMenu) {
				modMenu.fluidStackFilterSync(this);
			}
		}));
	}
}
