package com.sonamorningstar.eternalartifacts.network.tesseract;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.network.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record TesseractNetworkWhitelistToServer(String name, UUID networkID) implements CustomPacketPayload {
	
	public static final ResourceLocation ID = new ResourceLocation(MODID, "add_network_whitelist");
	
	public static TesseractNetworkWhitelistToServer create(FriendlyByteBuf buf) {
		return new TesseractNetworkWhitelistToServer(buf.readUtf(), buf.readUUID());
	}
	
	public static TesseractNetworkWhitelistToServer create(String name, UUID networkID) {
		return new TesseractNetworkWhitelistToServer(name, networkID);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeUtf(name);
		buff.writeUUID(networkID);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
			TesseractNetworks networks = TesseractNetworks.get(player.level());
			if (networks != null) {
				TesseractNetwork<?> network = networks.getNetworkCache().get(networkID);
				if (network != null && network.getOwner().getId().equals(player.getUUID())) {
					ServerLevel sLevel = (ServerLevel) player.level();
					boolean added = false;
					for (ServerPlayer serverPlayer : sLevel.players()) {
						if (serverPlayer.getGameProfile().getName().equals(name)) {
							networks.mutateNetwork(network, n -> n.addWhiteList(serverPlayer));
							added = true;
						}
					}
					if (!added) {
						networks.mutateNetwork(network, n -> n.queuePlayerForWhitelist(name));
					}
					Channel.sendToPlayer(new TesseractNetworksToClient(networks.getTesseractNetworks()), (ServerPlayer) player);
				}
			}
		}));
	}
}