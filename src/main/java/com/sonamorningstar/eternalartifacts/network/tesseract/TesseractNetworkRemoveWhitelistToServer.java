package com.sonamorningstar.eternalartifacts.network.tesseract;

import com.mojang.datafixers.util.Either;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.HashSet;
import java.util.UUID;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record TesseractNetworkRemoveWhitelistToServer(Either<UUID, String> whitelisted, UUID networkID) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "remove_network_whitelist");
	
	public static TesseractNetworkRemoveWhitelistToServer create(FriendlyByteBuf buf) {
		UUID networkID = buf.readUUID();
		String string = buf.readUtf();
		if ("#DUMMY#".equals(string)) {
			return new TesseractNetworkRemoveWhitelistToServer(Either.left(buf.readUUID()), networkID);
		}
		return new TesseractNetworkRemoveWhitelistToServer(Either.right(string), buf.readUUID());
	}
	
	public static TesseractNetworkRemoveWhitelistToServer create(Either<UUID, String> whitelisted, UUID networkID) {
		return new TesseractNetworkRemoveWhitelistToServer(whitelisted, networkID);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		whitelisted.ifLeft(uuid -> {
			buff.writeUtf("#DUMMY#");
			buff.writeUUID(uuid);
		});
		whitelisted.ifRight(name -> {
			buff.writeUtf(name);
			buff.writeUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
		});
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
					whitelisted.ifLeft(uuid -> networks.mutateNetwork(network, n -> n.getWhitelistedPlayers().removeIf(p -> p.getId().equals(uuid))));
					whitelisted.ifRight(name -> networks.mutateNetwork(network, n -> n.getPendingWhitelistPlayers().removeIf(p -> p.equals(name))));
					Channel.sendToPlayer(new TesseractNetworksToClient(new HashSet<>(networks.getNetworksForPlayer(player))), (ServerPlayer) player);
				}
			}
		}));
	}
}
