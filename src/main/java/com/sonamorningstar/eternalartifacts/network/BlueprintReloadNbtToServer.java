package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record BlueprintReloadNbtToServer(ItemStack blueprint, NonNullList<ItemStack> items) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "blueprint_nbt_reload");

    public static BlueprintReloadNbtToServer create(FriendlyByteBuf buf) {
        return new BlueprintReloadNbtToServer(buf.readItem(), buf.readCollection(NonNullList::createWithCapacity, FriendlyByteBuf::readItem));
    }

    public static BlueprintReloadNbtToServer create(ItemStack blueprint, NonNullList<ItemStack> items) {
        return new BlueprintReloadNbtToServer(blueprint, items);
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeItem(blueprint);
        buff.writeCollection(items, FriendlyByteBuf::writeItem);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            if (blueprint.getItem() instanceof BlueprintItem) {
                //BlueprintItem.updateFakeItems(blueprint, items);
            }
        }));
    }
}
