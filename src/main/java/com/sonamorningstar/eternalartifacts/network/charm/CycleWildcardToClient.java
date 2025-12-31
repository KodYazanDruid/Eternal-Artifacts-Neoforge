package com.sonamorningstar.eternalartifacts.network.charm;

import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record CycleWildcardToClient(int entityId, boolean value) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "cycle_wildcard");
	
	public static CycleWildcardToClient create(FriendlyByteBuf buf) {
		return new CycleWildcardToClient(buf.readVarInt(), buf.readBoolean());
	}
	
	public static CycleWildcardToClient create(int entityId, boolean value) {
		return new CycleWildcardToClient(entityId, value);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeVarInt(entityId);
		buff.writeBoolean(value);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ClientProxy.handleCycleWildcard(this, ctx);
	}
}
