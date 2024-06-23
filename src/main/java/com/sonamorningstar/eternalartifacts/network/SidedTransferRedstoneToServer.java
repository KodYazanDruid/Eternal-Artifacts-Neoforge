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

public record SidedTransferRedstoneToServer(int index, SidedTransferMachineBlockEntity.RedstoneType type, BlockPos pos) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(MODID, "sidedtransferredstone");

    public static SidedTransferRedstoneToServer create(FriendlyByteBuf buf) {
        return new SidedTransferRedstoneToServer(buf.readInt(), buf.readEnum(SidedTransferMachineBlockEntity.RedstoneType.class), buf.readBlockPos());
    }

    public static SidedTransferRedstoneToServer create(int index, SidedTransferMachineBlockEntity.RedstoneType type, BlockPos pos) {
        return new SidedTransferRedstoneToServer(index, type, pos);
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
            if(entity instanceof SidedTransferMachineBlockEntity<?> sided) {
                Map<Integer, SidedTransferMachineBlockEntity.RedstoneType> redstoneConfigs = sided.getRedstoneConfigs();
                redstoneConfigs.put(index, type);
                sided.sendUpdate();
                level.updateNeighborsAt(pos, entity.getBlockState().getBlock());
            }
        }));
    }
}
