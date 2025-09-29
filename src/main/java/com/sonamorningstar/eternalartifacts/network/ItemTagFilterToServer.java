package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.AbstractPipeFilterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record ItemTagFilterToServer(int containerId, int index, TagKey<Item> tag) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "item_tag_filter_to_server");
	
	public static ItemTagFilterToServer create(FriendlyByteBuf buf) {
		return new ItemTagFilterToServer(buf.readByte(), buf.readVarInt(),
			TagKey.create(Registries.ITEM, buf.readResourceLocation())
		);
	}
	
	public static ItemTagFilterToServer create(int id, int index, TagKey<Item> tag) {
		return new ItemTagFilterToServer(id, index, tag);
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
			if (menu.containerId == containerId && menu instanceof AbstractPipeFilterMenu modMenu) {
				modMenu.itemTagFilterSynch(this);
			}
		}));
	}
}
