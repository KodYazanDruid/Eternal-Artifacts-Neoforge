package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.network.base.ClientPayload;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record MachineWorkingStateToClient(BlockPos pos, boolean state) implements ClientPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "machine_working_state_to_client");
	
	public static MachineWorkingStateToClient create(FriendlyByteBuf buff) {
		return new MachineWorkingStateToClient(buff.readBlockPos(), buff.readBoolean());
	}
	
	@Override
	public void handleOnClient(Minecraft minecraft) {
		BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		if (blockEntity instanceof Machine<?> machine) {
			machine.setWorking(state);
		}
	}
	
	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeBoolean(state);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
}
