package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.container.TesseractMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record AddTesseractNetworkToServer(String name, int ownerEntityId, int secIdx, int capIdx) implements CustomPacketPayload {
	
	public static final ResourceLocation ID = new ResourceLocation(MODID, "add_network");
	
	public static AddTesseractNetworkToServer create(FriendlyByteBuf buf) {
		return new AddTesseractNetworkToServer(buf.readUtf(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
	}
	
	public static AddTesseractNetworkToServer create(String name, int ownerEntityId, int secIdx, int capIdx) {
		return new AddTesseractNetworkToServer(name, ownerEntityId, secIdx, capIdx);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeUtf(name);
		buff.writeVarInt(ownerEntityId);
		buff.writeVarInt(secIdx);
		buff.writeVarInt(capIdx);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
			if (player.getId() == ownerEntityId && player.containerMenu instanceof TesseractMenu menu) {
				int secCount = TesseractNetwork.Access.values().length;
				menu.addNetwork(name, player, Mth.clamp(secIdx, 0, secCount - 1), capIdx);
			}
		}));
	}
}
