package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.container.TankKnapsackMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record TankKnapsackTransferToServer(int tankNo, int button) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "tank_knapsack_screen_interact");

    public static TankKnapsackTransferToServer create(FriendlyByteBuf buff) { return new TankKnapsackTransferToServer(buff.readVarInt(), buff.readVarInt()); }
    public static TankKnapsackTransferToServer create(int tankNo, int button) { return new TankKnapsackTransferToServer(tankNo, button); }

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
                if (player.containerMenu instanceof TankKnapsackMenu knapsackMenu && knapsackMenu.stillValid(player)) {
                    knapsackMenu.handleTransfers(tankNo, button);
                }
            }
        }));
    }
}
