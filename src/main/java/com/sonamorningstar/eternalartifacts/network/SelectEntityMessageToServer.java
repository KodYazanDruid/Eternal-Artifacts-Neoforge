package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.item.EntityCatalogueItem;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record SelectEntityMessageToServer(int index) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "select_entity");
	
	public static SelectEntityMessageToServer create(FriendlyByteBuf buf) {
		return new SelectEntityMessageToServer(buf.readVarInt());
	}
	
	public static SelectEntityMessageToServer create(int index) {
		return new SelectEntityMessageToServer(index);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeVarInt(index);
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
			ItemStack modified;
			if (player.getMainHandItem().getItem() instanceof EntityCatalogueItem) modified = player.getMainHandItem();
			else if (player.getOffhandItem().getItem() instanceof EntityCatalogueItem) modified = player.getOffhandItem();
			else return;
			
			EntityCatalogueItem.setIndex(modified, index);
		}));
	}
}
