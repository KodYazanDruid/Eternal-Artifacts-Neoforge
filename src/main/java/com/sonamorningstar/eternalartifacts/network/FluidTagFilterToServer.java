package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.FilterSyncable;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.network.base.PacketHelper.*;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record FluidTagFilterToServer(int containerId, int index, TagKey<Fluid> tag) implements ServerPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "fluid_tag_filter_to_server");
	
	public static FluidTagFilterToServer create(FriendlyByteBuf buf) {
		return new FluidTagFilterToServer(
			readContainerId(buf),
			readIndex(buf),
			TagKey.create(Registries.FLUID, buf.readResourceLocation())
		);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		writeContainerId(buff, containerId);
		writeIndex(buff, index);
		buff.writeResourceLocation(tag.location());
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	@Override
	public void handleOnServer(ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId == containerId && menu instanceof FilterSyncable syncable) {
			syncable.fluidTagFilterSync(this);
		}
	}
}
