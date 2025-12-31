package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record SyncCharmTagsToClient(int charmSlot, CompoundTag tag) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "sync_charm_tags");
	
	public static SyncCharmTagsToClient create(FriendlyByteBuf buf) {
		return new SyncCharmTagsToClient(buf.readVarInt(), buf.readNbt());
	}
	
	public static SyncCharmTagsToClient create(int charmSlot, CompoundTag tag) {
		return new SyncCharmTagsToClient(charmSlot, tag);
	}
	
	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(charmSlot);
		buf.writeNbt(tag);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isClientbound()){
			ctx.workHandler().execute(() -> {
				if (ctx.player().isPresent()) {
					Player player = ctx.player().get();
					CharmStorage charmStorage = CharmStorage.get(player);
					charmStorage.getStackInSlot(charmSlot).setTag(tag);
				}
			});
		}
	}
}
