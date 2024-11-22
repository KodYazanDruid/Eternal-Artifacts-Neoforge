package com.sonamorningstar.eternalartifacts.network.charm;

import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record ReloadTabs() implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "reload_tabs");

    public static ReloadTabs create(FriendlyByteBuf buf) {
        return new ReloadTabs();
    }

    public static ReloadTabs create() {
        return new ReloadTabs();
    }

    @Override
    public void write(FriendlyByteBuf buff) {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        if(ctx.flow().isClientbound()){
            ctx.workHandler().execute(() -> {
                if (ctx.player().isPresent()) {
                    /*if (TabHandler.INSTANCE != null) TabHandler.INSTANCE.reloadTabs();*/
                }
            });
        }
    }
}
