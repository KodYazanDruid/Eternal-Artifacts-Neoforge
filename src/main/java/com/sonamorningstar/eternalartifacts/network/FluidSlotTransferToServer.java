package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record FluidSlotTransferToServer(int tankNo, int button) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "fluid_slot_transfer");

    public static FluidSlotTransferToServer create(FriendlyByteBuf buff) { return new FluidSlotTransferToServer(buff.readVarInt(), buff.readVarInt()); }
    public static FluidSlotTransferToServer create(int tankNo, int button) { return new FluidSlotTransferToServer(tankNo, button); }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeVarInt(tankNo);
        buff.writeVarInt(button);
    }

    @Override
    public ResourceLocation id() {return ID;}

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            if (player.level().isLoaded(player.blockPosition())) {
                if (player.containerMenu instanceof AbstractModContainerMenu menu && menu.stillValid(player)) {
                    menu.handleFluidTankTransfer(tankNo, button);
                }
            }
        }));
    }
}
