package com.sonamorningstar.eternalartifacts.network.endernotebook;

import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record OpenItemStackScreenToClient(ItemStack stack) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "open_itemstack_screen");

    public static OpenItemStackScreenToClient create(FriendlyByteBuf buf) {
        return new OpenItemStackScreenToClient(buf.readItem());
    }

    public static OpenItemStackScreenToClient create(ItemStack stack) {
        return new OpenItemStackScreenToClient(stack);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        if(ctx.flow().isClientbound()){
            ctx.workHandler().execute(() -> {
                if (ctx.player().isPresent()) {
                    ClientProxy.requestScreen(stack);
                }
			});
        }
    }
}
