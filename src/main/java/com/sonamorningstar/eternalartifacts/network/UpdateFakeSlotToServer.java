package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.network.base.PacketHelper.*;
import static com.sonamorningstar.eternalartifacts.network.base.MenuHelper.handleMenu;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record UpdateFakeSlotToServer(int containerId, int index, ItemStack slotItem) implements ServerPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "update_fake_slot_to_server");

    public static UpdateFakeSlotToServer create(FriendlyByteBuf buf) {
        return new UpdateFakeSlotToServer(readContainerId(buf), readIndex(buf), buf.readItem());
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        writeContainerId(buff, containerId);
        writeIndex(buff, index);
        buff.writeItem(slotItem);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        handleMenu(player, containerId, AbstractModContainerMenu.class,
            menu -> menu.fakeSlotSynch(this));
    }
}
