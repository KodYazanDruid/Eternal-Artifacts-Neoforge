package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.block.entity.AnvilinatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record PacketAnvilatorSwitchToServer(boolean enableNaming, BlockPos pos, String naming) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(MODID, "switch_anvilator");

    public static PacketAnvilatorSwitchToServer create(FriendlyByteBuf buf) {
        return new PacketAnvilatorSwitchToServer(buf.getBoolean(0), buf.readBlockPos(), buf.readUtf());
    }

    public static PacketAnvilatorSwitchToServer create(boolean enableNaming, BlockPos pos, String naming) {
        return new PacketAnvilatorSwitchToServer(enableNaming, pos, naming);
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(enableNaming);
        pBuffer.writeBlockPos(pos);
        pBuffer.writeUtf(naming);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> {
            ctx.player().ifPresent(player -> {
                if (player.level().isLoaded(pos) && player.level().getBlockEntity(pos) instanceof AnvilinatorBlockEntity anvilator) {
                    anvilator.setEnableNaming(enableNaming);
                    anvilator.setName(naming);
                }
            });
        });
    }
}
