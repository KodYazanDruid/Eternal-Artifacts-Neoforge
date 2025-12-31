package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ServerPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.network.base.PacketHelper.*;

@RegisterPacket(side = RegisterPacket.PacketSide.SERVER)
public record FluidSlotTransferToServer(int tankNo, int button) implements ServerPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "fluid_slot_transfer");

    public static FluidSlotTransferToServer create(FriendlyByteBuf buff) {
        return new FluidSlotTransferToServer(readIndex(buff), readIndex(buff));
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        writeIndex(buff, tankNo);
        writeIndex(buff, button);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.level().isLoaded(player.blockPosition())) {
            if (player.containerMenu instanceof AbstractModContainerMenu menu && menu.stillValid(player)) {
                menu.handleFluidTankTransfer(tankNo, button);
            }
        }
    }
}
