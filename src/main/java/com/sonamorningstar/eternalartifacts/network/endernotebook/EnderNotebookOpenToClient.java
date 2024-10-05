package com.sonamorningstar.eternalartifacts.network.endernotebook;

import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record EnderNotebookOpenToClient(ItemStack book) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "open_endernotebook_screen");

    public static EnderNotebookOpenToClient create(FriendlyByteBuf buf) {
        return new EnderNotebookOpenToClient(buf.readItem());
    }

    public static EnderNotebookOpenToClient create(ItemStack book) {
        return new EnderNotebookOpenToClient(book);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(book);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        if(ctx.flow().isClientbound()){
            ctx.workHandler().execute(() -> {
                if (ctx.player().isPresent()) {
                    ClientProxy.openEnderNotebook(book);
                };
            });
        }
    }
}
