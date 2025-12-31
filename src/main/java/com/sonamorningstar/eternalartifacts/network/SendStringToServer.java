package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record SendStringToServer(int containerId, String link) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "set_link_to_server");
	
	public static SendStringToServer create(FriendlyByteBuf buf) {
		return new SendStringToServer(buf.readVarInt(), buf.readUtf());
	}
	
	public static SendStringToServer create(int containerId, String link) {
		return new SendStringToServer(containerId, link);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeVarInt(containerId);
		buff.writeUtf(link);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
			if (player.containerMenu.containerId == containerId && player.containerMenu instanceof AbstractModContainerMenu menu) {
				menu.receiveStringPkt(this);
			}
		}));
	}
}
