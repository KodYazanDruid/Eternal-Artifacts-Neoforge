package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.PipeFilterItemMenu;
import com.sonamorningstar.eternalartifacts.container.PipeFilterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record FluidTagFilterToServer(int containerId, int index, TagKey<Fluid> tag) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "fluid_tag_filter_to_server");
	
	public static FluidTagFilterToServer create(FriendlyByteBuf buf) {
		return new FluidTagFilterToServer(buf.readByte(), buf.readVarInt(),
			TagKey.create(Registries.FLUID, buf.readResourceLocation())
		);
	}
	
	public static FluidTagFilterToServer create(int id, int index, TagKey<Fluid> tag) {
		return new FluidTagFilterToServer(id, index, tag);
	}
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeByte(containerId);
		buff.writeVarInt(index);
		buff.writeResourceLocation(tag.location());
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
			AbstractContainerMenu menu = player.containerMenu;
			if (menu.containerId == containerId && menu instanceof PipeFilterMenu modMenu) {
				modMenu.fluidTagFilter(this);
			}
			if (menu.containerId == containerId && menu instanceof PipeFilterItemMenu modMenu) {
				modMenu.fluidTagFilter(this);
			}
		}));
	}
}
