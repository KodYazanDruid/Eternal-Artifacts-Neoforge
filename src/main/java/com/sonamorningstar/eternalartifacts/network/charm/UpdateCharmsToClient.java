package com.sonamorningstar.eternalartifacts.network.charm;

import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record UpdateCharmsToClient(int playerId, NonNullList<ItemStack> items) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "update_charms");

    public static UpdateCharmsToClient create(FriendlyByteBuf buf) {
        return new UpdateCharmsToClient(buf.readVarInt(), buf.readCollection(NonNullList::createWithCapacity, FriendlyByteBuf::readItem));
    }

    public static UpdateCharmsToClient create(int playerId, NonNullList<ItemStack> items) {
        return new UpdateCharmsToClient(playerId, items);
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeVarInt(playerId);
        buff.writeCollection(items, FriendlyByteBuf::writeItem);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ClientProxy.handleUpdateCharms(this, ctx);
    }
}
