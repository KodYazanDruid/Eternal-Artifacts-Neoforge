package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.network.proxy.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record UpdateEntityEnergyToClient(int entityId, int energy) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "update_energy_entity");

    public static UpdateEntityEnergyToClient create(FriendlyByteBuf buf) {
        return new UpdateEntityEnergyToClient(buf.readVarInt(), buf.readInt());
    }

    public static UpdateEntityEnergyToClient create(int entityId, int energy) {
        return new UpdateEntityEnergyToClient(entityId, energy);
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeVarInt(entityId);
        buff.writeInt(energy);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ClientProxy.handleUpdateSheepEnergy(this, ctx);
    }
}
