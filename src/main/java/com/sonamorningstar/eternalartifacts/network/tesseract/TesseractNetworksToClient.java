package com.sonamorningstar.eternalartifacts.network.tesseract;

import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetworks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.HashSet;
import java.util.Set;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record TesseractNetworksToClient(Set<TesseractNetwork<?>> networks) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "sync_tesseract_networks_to_client");
	
	public static TesseractNetworksToClient create(FriendlyByteBuf buf) {
		return new TesseractNetworksToClient(buf.readCollection(
			HashSet::new,
			b -> TesseractNetwork.fromNBT(b.readNbt())
		));
	}
	
	public static TesseractNetworksToClient create(Set<TesseractNetwork<?>> networks) {
		return new TesseractNetworksToClient(networks);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeCollection(
			networks,
			(b, network) -> {
				CompoundTag tag = new CompoundTag();
				network.writeToNBT(tag);
				b.writeNbt(tag);
			}
		);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isClientbound()){
			ctx.workHandler().execute(() -> ctx.player().ifPresent(player -> {
				TesseractNetworks.applyOnClient(networks);
			}));
		}
	}
}
