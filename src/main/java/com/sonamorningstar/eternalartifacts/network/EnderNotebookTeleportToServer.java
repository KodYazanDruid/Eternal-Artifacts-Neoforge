package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record EnderNotebookTeleportToServer(ResourceKey<Level> target, BlockPos pos) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(MODID, "endernotebook_teleport");

    public static EnderNotebookTeleportToServer create(FriendlyByteBuf buf) {
        return new EnderNotebookTeleportToServer(buf.readResourceKey(Registries.DIMENSION), buf.readBlockPos());
    }

    public static EnderNotebookTeleportToServer create(ResourceKey<Level> dimension, BlockPos pos) {
        return new EnderNotebookTeleportToServer(dimension, pos);
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceKey(target);
        pBuffer.writeBlockPos(pos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            ServerLevel dest = player.getServer().getLevel(target);
            PlayerHelper.teleportToDimension(((ServerPlayer) player), dest, Vec3.atCenterOf(pos));
        }));
    }

}
