package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record SidedTransferAutoSaveToServer(int index, boolean auto, BlockPos pos) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "sidedtransferauto");

    public static SidedTransferAutoSaveToServer create(FriendlyByteBuf buf) {
        return new SidedTransferAutoSaveToServer(buf.readInt(), buf.readBoolean(), buf.readBlockPos());
    }

    public static SidedTransferAutoSaveToServer create(int index, boolean auto, BlockPos pos) {
        return new SidedTransferAutoSaveToServer(index, auto, pos);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(index);
        buffer.writeBoolean(auto);
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
            if(entity instanceof SidedTransferMachineBlockEntity<?> sided) {
                Map<Integer, Boolean> autoConfigs = sided.getAutoConfigs();
                autoConfigs.put(index, auto);
                sided.sendUpdate();
            }
        }));
    }
}
