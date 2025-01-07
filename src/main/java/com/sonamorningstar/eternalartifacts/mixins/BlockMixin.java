package com.sonamorningstar.eternalartifacts.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.sonamorningstar.eternalartifacts.api.item.ChiselBlockPlaceContext;
import com.sonamorningstar.eternalartifacts.content.item.ChiselItem;
import com.sonamorningstar.eternalartifacts.event.common.CommonEvents;
import com.sonamorningstar.eternalartifacts.network.BlockPlaceOnClient;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import com.sonamorningstar.eternalartifacts.util.collections.ListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Debug
@Mixin(Block.class)
public abstract class BlockMixin {

    @Unique
    private boolean eternal_Artifacts_Neoforge$isSuccessful = false;

    @WrapWithCondition(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Z)V"))
    private boolean playerDestroy(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, boolean dropXp) {
        return !(tool.getItem() instanceof ChiselItem) ||
                (tool.getItem() instanceof ChiselItem && !eternal_Artifacts_Neoforge$isSuccessful);
    }

    @Inject(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Z)V"))
    private void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        eternal_Artifacts_Neoforge$isSuccessful = false;
        if(tool.getItem() instanceof ChiselItem) {
            BlockHitResult hitResult = CommonEvents.eternal_Artifacts_Neoforge$cachedRay;
            if(hitResult == null) return;
            ListIterator<ItemStack> blocks = PlayerHelper.itemWithClassIterable(player, BlockItem.class);
            ItemStack offHand = player.getOffhandItem();
            if(!offHand.isEmpty() && offHand.getItem() instanceof BlockItem) blocks.putFirst(offHand);
            eternal_Artifacts_Neoforge$insertItems(player, level, state, pos, blockEntity, tool);
            eternal_Artifacts_Neoforge$tryPlace(blocks, player, state, pos, hitResult);
            eternal_Artifacts_Neoforge$isSuccessful = true;
        }
    }

    @Unique
    private void eternal_Artifacts_Neoforge$insertItems(Player player, Level level, BlockState state, BlockPos pos, BlockEntity blockEntity, ItemStack tool) {
        List<ItemStack> drops = new ArrayList<>();
        if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
            drops = BlockHelper.getBlockDrops(serverLevel, state, pos, tool, blockEntity, serverPlayer);
        }
        drops.forEach(drop -> PlayerHelper.giveItemOrPop(player, drop, pos.getX(), pos.getY(), pos.getZ()));
    }

    @Unique
    private void eternal_Artifacts_Neoforge$tryPlace(Iterator<ItemStack> iterator, Player player,
                                                     BlockState state, BlockPos pos, BlockHitResult hitResult) {
        if (iterator.hasNext()) {
            ItemStack blockItemStack = iterator.next();
            if(!blockItemStack.isEmpty() &&
                    blockItemStack.getItem() instanceof BlockItem blockItem) {
                if(blockItem.getBlock() == state.getBlock()) {
                    eternal_Artifacts_Neoforge$tryPlace(iterator, player, state, pos, hitResult);
                    return;
                }
                ItemStack clientStack = blockItemStack.copy();
                ChiselBlockPlaceContext ctx = new ChiselBlockPlaceContext(player, InteractionHand.MAIN_HAND, blockItemStack, pos, hitResult);
                blockItem.updatePlacementContext(ctx);
                ctx.replaceClicked = true;
                InteractionResult result = blockItem.place(ctx);
                Channel.sendToPlayer(new BlockPlaceOnClient(clientStack, pos, InteractionHand.MAIN_HAND, hitResult), (ServerPlayer) player);
                if(!result.consumesAction()) eternal_Artifacts_Neoforge$tryPlace(iterator, player, state, pos, hitResult);
            }
        }
    }
}
