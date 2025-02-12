package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record RebuildTesseractPanelToClient() implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "tesseract_networks_to_client");
	
	public static RebuildTesseractPanelToClient create(FriendlyByteBuf buff) {
		return new RebuildTesseractPanelToClient();
	}
	
	public static RebuildTesseractPanelToClient create() {
		return new RebuildTesseractPanelToClient();
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {

	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isClientbound()){
			ctx.workHandler().execute(() -> ctx.player().ifPresent(player -> {
				ClientProxy.rebuildTesseractPanel();
			}));
		}
	}
}
