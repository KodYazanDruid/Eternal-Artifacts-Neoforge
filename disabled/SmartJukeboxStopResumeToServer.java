package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.block.entity.SmartJukeboxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record SmartJukeboxStopResumeToServer(BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "smartjukeboxstopresume");

    public static SmartJukeboxStopResumeToServer create(FriendlyByteBuf buf) {
        return new SmartJukeboxStopResumeToServer(buf.readBlockPos());
    }

    public static SmartJukeboxStopResumeToServer create(BlockPos pos) {
        return new SmartJukeboxStopResumeToServer(pos);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(()-> ctx.player().ifPresent(player -> {
            Level level = player.level();
            BlockEntity entity = level.getBlockEntity(pos);
            if(entity instanceof SmartJukeboxBlockEntity jukebox) {
                if (jukebox.isPlaying()) jukebox.pauseMusic();
                else jukebox.resumeMusic();
            }
        }));
    }

}
