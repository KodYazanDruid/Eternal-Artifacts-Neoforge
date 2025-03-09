package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.registrar.TabType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record OpenTabMenuToServer(TabType<? extends AbstractInventoryTab> type) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(MODID, "open_menu");

    public static OpenTabMenuToServer create(FriendlyByteBuf buf) {
        return new OpenTabMenuToServer(ModRegistries.TAB_TYPE.get(buf.readResourceLocation()));
    }

    public static OpenTabMenuToServer create(TabType<? extends AbstractInventoryTab> type) {
        return new OpenTabMenuToServer(type);
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeResourceLocation(ModRegistries.TAB_TYPE.getKey(type));
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            AbstractInventoryTab tab = type.create(new FriendlyByteBuf(Unpooled.buffer()));
            serverPlayer.openMenu(tab, tab.getBytes(ctx.player().get()));
        }));
    }
}
