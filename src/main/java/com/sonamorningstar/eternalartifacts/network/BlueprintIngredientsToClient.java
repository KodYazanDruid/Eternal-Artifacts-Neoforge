package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import com.sonamorningstar.eternalartifacts.container.slot.BlueprintFakeSlot;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record BlueprintIngredientsToClient(int containerId, NonNullList<Ingredient> ingredients) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(MODID, "blueprint_ingredients_to_client");
	
	public static BlueprintIngredientsToClient create(FriendlyByteBuf buff) {
		return new BlueprintIngredientsToClient(buff.readVarInt(), buff.readCollection(NonNullList::createWithCapacity, Ingredient::fromNetwork));
	}
	
	public static BlueprintIngredientsToClient create(int containerId, NonNullList<Ingredient> ingredients) {
		return new BlueprintIngredientsToClient(containerId, ingredients);
	}
	
	@Override
	public void write(FriendlyByteBuf buff) {
		buff.writeVarInt(containerId);
		buff.writeCollection(ingredients, (buf, ingredient) -> ingredient.toNetwork(buf));
	}
	
	@Override
	public ResourceLocation id() {
		return ID;
	}
	
	public void handle(PlayPayloadContext ctx) {
		if(ctx.flow().isClientbound()){
			ClientProxy.handleBlueprintIngredientsToClient(this);
		}
	}
}
