package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.block.entity.EnergyDistributor;
import com.sonamorningstar.eternalartifacts.network.base.ClientPayload;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record EnergyDistributorTargetsToClient(BlockPos pos, LongList targets) implements ClientPayload {
	
	public static final ResourceLocation ID = new ResourceLocation(MODID, "energy_distributor_targets_to_client");
	
	public static EnergyDistributorTargetsToClient create(FriendlyByteBuf buff) {
		return new EnergyDistributorTargetsToClient(buff.readBlockPos(), buff.readCollection(LongArrayList::new, FriendlyByteBuf::readLong));
	}
	
	@Override
	public void handleOnClient(Minecraft minecraft) {
		BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		if (blockEntity instanceof EnergyDistributor distributor) {
			distributor.targets = targets;
		}
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeCollection(targets, FriendlyByteBuf::writeLong);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
}
