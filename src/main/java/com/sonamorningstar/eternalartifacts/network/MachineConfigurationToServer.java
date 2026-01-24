package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.network.base.PacketHelper.*;
import static com.sonamorningstar.eternalartifacts.network.base.BlockEntityHelper.handleBlockEntity;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record MachineConfigurationToServer(BlockPos pos, ResourceLocation location, FriendlyByteBuf data) implements ServerPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "machine_configuration");
	
	public static MachineConfigurationToServer create(FriendlyByteBuf buf) {
		return new MachineConfigurationToServer(
			readPos(buf),
			buf.readResourceLocation(),
			new FriendlyByteBuf(buf.readBytes(buf.readVarInt()))
		);
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		writePos(buffer, pos);
		buffer.writeResourceLocation(location);
		buffer.writeVarInt(data.readableBytes());
		buffer.writeBytes(data, data.readerIndex(), data.readableBytes());
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	@Override
	public void handleOnServer(ServerPlayer player) {
		handleBlockEntity(player, pos, ModBlockEntity.class, blockEntity -> {
			MachineConfiguration configs = blockEntity.getConfiguration();
			Config config = configs.get(location);
			if (config == null) return;
			config.readFromClient(data);
			blockEntity.sendUpdate();
			blockEntity.getLevel().invalidateCapabilities(pos);
		});
	}
}
