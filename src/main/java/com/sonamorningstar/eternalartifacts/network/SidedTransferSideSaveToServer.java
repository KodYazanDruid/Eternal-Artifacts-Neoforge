package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.content.block.entity.SidedTransferBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Map;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public record SidedTransferSideSaveToServer(int index, SidedTransferBlockEntity.TransferType type, BlockPos pos) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(MODID, "sidedtransferside");

    public static SidedTransferSideSaveToServer create(FriendlyByteBuf buf) {
        return new SidedTransferSideSaveToServer(buf.readInt(), buf.readEnum(SidedTransferBlockEntity.TransferType.class), buf.readBlockPos());
    }

    public static SidedTransferSideSaveToServer create(int index, SidedTransferBlockEntity.TransferType type, BlockPos pos) {
        return new SidedTransferSideSaveToServer(index, type, pos);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(index);
        buffer.writeEnum(type);
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
            if(entity instanceof SidedTransferBlockEntity<?> sided) {
                Map<Integer, SidedTransferBlockEntity.TransferType> sideConfigs = sided.getSideConfigs();
                sideConfigs.put(index, type);
                sided.sendUpdate();
                sided.invalidateCapabilities();
                level.updateNeighborsAt(pos, entity.getBlockState().getBlock());
            }
        }));
    }
}
