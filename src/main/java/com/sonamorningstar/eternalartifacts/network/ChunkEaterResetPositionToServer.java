package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.block.entity.ChunkEaterBlockEntity;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record ChunkEaterResetPositionToServer(BlockPos machinePos) implements ServerPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "chunk_eater_reset_position");
	
	public static ChunkEaterResetPositionToServer create(FriendlyByteBuf buffer) {
		BlockPos pos = buffer.readBlockPos();
		return new ChunkEaterResetPositionToServer(pos);
	}
	
	@Override
	public void handleOnServer(ServerPlayer player) {
		BlockEntity blockEntity = player.level().getBlockEntity(machinePos);
		if (blockEntity instanceof ChunkEaterBlockEntity chunkEater && chunkEater.isMaster()) {
			chunkEater.resetMining();
		}
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(machinePos);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
}
