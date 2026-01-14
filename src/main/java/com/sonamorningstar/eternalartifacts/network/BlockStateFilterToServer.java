package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.FilterSyncable;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.network.base.PacketHelper.*;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record BlockStateFilterToServer(int containerId, int index, @Nullable BlockState blockState) implements ServerPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "block_state_filter_to_server");
	
	public static BlockStateFilterToServer create(FriendlyByteBuf buf) {
		int containerId = readContainerId(buf);
		int index = readIndex(buf);
		boolean hasState = buf.readBoolean();
		BlockState state = null;
		if (hasState) {
			ResourceLocation blockId = buf.readResourceLocation();
			Block block = BuiltInRegistries.BLOCK.get(blockId);
			state = block.defaultBlockState();
		}
		return new BlockStateFilterToServer(containerId, index, state);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		writeContainerId(buff, containerId);
		writeIndex(buff, index);
		boolean hasState = blockState != null && !blockState.isAir();
		buff.writeBoolean(hasState);
		if (hasState) {
			ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());
			buff.writeResourceLocation(blockId);
		}
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	@Override
	public void handleOnServer(ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId == containerId && menu instanceof FilterSyncable syncable) {
			syncable.blockStateFilterSync(this);
		}
	}
}