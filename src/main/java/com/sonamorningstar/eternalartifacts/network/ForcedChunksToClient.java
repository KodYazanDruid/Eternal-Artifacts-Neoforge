package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.forceload.ForceLoadManager;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ChunkLoader;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.HashSet;
import java.util.Set;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record ForcedChunksToClient(Set<ForceLoadManager.ForcedChunkPos> forcedChunks, BlockPos pos) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "forced_chunks_to_client");
	
	public static ForcedChunksToClient create(FriendlyByteBuf buf) {
		return new ForcedChunksToClient(buf.readCollection(HashSet::new, ForceLoadManager.ForcedChunkPos::read), buf.readBlockPos());
	}
	
	public static ForcedChunksToClient create(Set<ForceLoadManager.ForcedChunkPos> forcedChunks, BlockPos pos) {
		return new ForcedChunksToClient(forcedChunks, pos);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeCollection(forcedChunks, ForceLoadManager.ForcedChunkPos::write);
		buff.writeBlockPos(pos);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isClientbound()) {
			ctx.workHandler().execute(() -> ctx.player().ifPresent(player -> {
				Level level = player.level();
				if (level.isLoaded(pos)) {
					BlockEntity be = level.getBlockEntity(pos);
					if (be instanceof ChunkLoader loader) {
						loader.receiveFromServer(forcedChunks);
					}
				}
			}));
		}
	}
}
