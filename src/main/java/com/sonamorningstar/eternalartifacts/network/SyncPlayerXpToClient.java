package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.network.base.ClientPayload;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record SyncPlayerXpToClient(int xpLevel, int totalXp, float xpProgress) implements ClientPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "sync_player_xp");
	
	public static SyncPlayerXpToClient create(FriendlyByteBuf buf) {
		return new SyncPlayerXpToClient(buf.readInt(), buf.readInt(), buf.readFloat());
	}
	
	@Override
	public void handleOnClient(Minecraft minecraft) {
		LocalPlayer local = minecraft.player;
		if (local != null) {
			local.experienceLevel = xpLevel;
			local.totalExperience = totalXp;
			local.experienceProgress = xpProgress;
		}
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(xpLevel);
		buffer.writeInt(totalXp);
		buffer.writeFloat(xpProgress);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
}
