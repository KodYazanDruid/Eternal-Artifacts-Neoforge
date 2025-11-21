package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.machine.MachineConfiguration;
import com.sonamorningstar.eternalartifacts.api.machine.config.Config;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record MachineConfigurationToServer(BlockPos pos, ResourceLocation location, FriendlyByteBuf data) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "machine_configuration");
	
	public static MachineConfigurationToServer create(FriendlyByteBuf buf) {
		return new MachineConfigurationToServer(buf.readBlockPos(), buf.readResourceLocation(), new FriendlyByteBuf(buf.readBytes(buf.readVarInt())));
	}
	
	public static MachineConfigurationToServer create(BlockPos pos, ResourceLocation location, FriendlyByteBuf data) {
		return new MachineConfigurationToServer(pos, location, data);
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeResourceLocation(location);
		buffer.writeVarInt(data.readableBytes());
		buffer.writeBytes(data, data.readerIndex(), data.readableBytes());
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
			BlockEntity entity = player.level().getBlockEntity(pos);
			if (!(entity instanceof ModBlockEntity mbe)) return;
			MachineConfiguration configs = mbe.getConfiguration();
			Config config = configs.get(location);
			if (config == null) return;
			config.readFromClient(data);
			mbe.sendUpdate();
		}));
	}
}
