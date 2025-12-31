package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import com.sonamorningstar.eternalartifacts.network.base.ClientPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.network.base.PacketHelper.*;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record UpdateEntityEnergyToClient(int entityId, int energy) implements ClientPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "update_energy_entity");

    public static UpdateEntityEnergyToClient create(FriendlyByteBuf buf) {
        return new UpdateEntityEnergyToClient(readEntityId(buf), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        writeEntityId(buff, entityId);
        buff.writeInt(energy);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Minecraft minecraft) {
        if (minecraft.player != null) {
            var level = minecraft.player.level();
            var entity = level.getEntity(entityId);
            if (entity != null) {
                var energyStorage = entity.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ENTITY, null);
                if (energyStorage instanceof com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage mes) {
                    mes.setEnergy(energy);
                }
            }
        }
    }
}
