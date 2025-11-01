package com.sonamorningstar.eternalartifacts.network.tesseract;

import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record RebuildTesseractPanelToClient(boolean clearSelected) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "tesseract_networks_to_client");
	
	public static RebuildTesseractPanelToClient create(FriendlyByteBuf buff) {
		return new RebuildTesseractPanelToClient(buff.readBoolean());
	}
	
	public static RebuildTesseractPanelToClient create(boolean clearSelected) {
		return new RebuildTesseractPanelToClient(clearSelected);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeBoolean(clearSelected);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isClientbound()){
			ctx.workHandler().execute(() -> ctx.player().ifPresent(player -> {
				ClientProxy.rebuildTesseractPanel(this);
			}));
		}
	}
}
