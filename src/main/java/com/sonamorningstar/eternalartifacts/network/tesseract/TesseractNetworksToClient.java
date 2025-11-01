package com.sonamorningstar.eternalartifacts.network.tesseract;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record TesseractNetworksToClient(TesseractNetwork<?> network) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "sync_tesseract_networks_to_client");
	
	public static TesseractNetworksToClient create(FriendlyByteBuf buf) {
		return new TesseractNetworksToClient(TesseractNetwork.fromNBT(buf.readNbt()));
	}
	
	public static TesseractNetworksToClient create(TesseractNetwork<?> network) {
		return new TesseractNetworksToClient(network);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		CompoundTag tag = new CompoundTag();
		network.writeToNBT(tag);
		buff.writeNbt(tag);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isClientbound()){
			ctx.workHandler().execute(() -> ctx.player().ifPresent(player -> {
				ClientProxy.syncTesseractNetwork(this);
			}));
		}
	}
}
