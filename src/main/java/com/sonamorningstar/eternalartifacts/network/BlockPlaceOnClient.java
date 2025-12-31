package com.sonamorningstar.eternalartifacts.network;

import com.sonamorningstar.eternalartifacts.api.item.ChiselBlockPlaceContext;
import com.sonamorningstar.eternalartifacts.network.base.RegisterPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@RegisterPacket(side = RegisterPacket.PacketSide.CLIENT)
public record BlockPlaceOnClient(ItemStack stack, BlockPos pos, InteractionHand hand, BlockHitResult hitResult) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MODID, "block_place");

    public static BlockPlaceOnClient create(FriendlyByteBuf buf) {
        return new BlockPlaceOnClient(buf.readItem(), buf.readBlockPos(), buf.readEnum(InteractionHand.class), buf.readBlockHitResult());
    }

    public static BlockPlaceOnClient create(ItemStack stack, BlockPos pos, InteractionHand hand, BlockHitResult hitResult) {
        return new BlockPlaceOnClient(stack, pos, hand, hitResult);
    }

    @Override
    public void write(FriendlyByteBuf buff) {
        buff.writeItem(stack);
        buff.writeBlockPos(pos);
        buff.writeEnum(hand);
        buff.writeBlockHitResult(hitResult);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        if(ctx.flow().isClientbound()){
            ctx.workHandler().execute(() -> {
                if (ctx.player().isPresent()) {
                    if (stack.getItem() instanceof BlockItem blockItem) {
                        var player = ctx.player().get();
                        ChiselBlockPlaceContext placeContext = new ChiselBlockPlaceContext(player, hand, stack, pos, hitResult);
                        blockItem.updatePlacementContext(placeContext);
                        blockItem.place(placeContext);
                    }
                }
            });
        }
    }
}
