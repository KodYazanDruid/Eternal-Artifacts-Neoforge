package com.sonamorningstar.eternalartifacts.network.protocol;

import com.sonamorningstar.eternalartifacts.content.block.entity.base.IButtonHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockEntityButtonPress implements CustomPacketPayload {

    public static ResourceLocation ID = new ResourceLocation(MODID, "block_entity_button_press");

    private final BlockPos pos;
    private final int key;
    private final int index;

    public BlockEntityButtonPress(BlockPos pos, int key, int index) {
        this.pos = pos;
        this.key = key;
        this.index = index;
    }

    public static BlockEntityButtonPress create(FriendlyByteBuf buf) {
        return new BlockEntityButtonPress(buf.readBlockPos(), buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeVarInt(key);
        buf.writeVarInt(index);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> ctx.player().ifPresent(player -> {
            BlockEntity be = player.level().getBlockEntity(pos);
            if (be instanceof IButtonHolder bh) {
                if (bh.buttonConsumerMap().containsKey(index)) {
                    bh.buttonConsumerMap().get(index).accept(key);
                }
            }
        }));
    }
}
