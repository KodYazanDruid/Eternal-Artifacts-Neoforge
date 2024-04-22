package com.sonamorningstar.eternalartifacts.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record ItemActivationToClient(ItemStack stack) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "display_item");

    public static ItemActivationToClient create(FriendlyByteBuf buf) {
        return new ItemActivationToClient(buf.readItem());
    }

    public static ItemActivationToClient create(ItemStack stack) {
        return new ItemActivationToClient(stack);
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeItem(stack);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        if(ctx.flow().isClientbound()){
            ctx.workHandler().execute(() -> {
                if (ctx.player().isPresent()) Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
            });
        }
    }
}
