package com.sonamorningstar.eternalartifacts.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record SavePlayerDataToClient(String key, int value) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "send_player_data_client");
	
	public static SavePlayerDataToClient create(FriendlyByteBuf buf) {
		return new SavePlayerDataToClient(buf.readUtf(), buf.readVarInt());
	}
	
	public static SavePlayerDataToClient create(String key, int entityId, int jumps) {
		return new SavePlayerDataToClient(key, jumps);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeUtf(key);
		buff.writeVarInt(value);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isClientbound()){
			ctx.workHandler().execute(() -> ctx.player().ifPresent(player -> {
				player.getPersistentData().putInt(key, value);
			}));
		}
	}
}
