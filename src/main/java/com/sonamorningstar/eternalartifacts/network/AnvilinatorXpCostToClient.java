package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.block.entity.Anvilinator;
import com.sonamorningstar.eternalartifacts.network.base.ClientPayload;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record AnvilinatorXpCostToClient(BlockPos pos, int cost) implements ClientPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "anvilinator_xp_cost_client");
	
	public static AnvilinatorXpCostToClient create(FriendlyByteBuf buf) {
		return new AnvilinatorXpCostToClient(buf.readBlockPos(), buf.readInt());
	}
	
	@Override
	public void handleOnClient(Minecraft minecraft) {
		if (minecraft.player != null) {
			var level = minecraft.player.level();
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof Anvilinator anvilinator) {
				anvilinator.setCurrentXpCost(cost);
			}
		}
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeInt(cost);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
}
