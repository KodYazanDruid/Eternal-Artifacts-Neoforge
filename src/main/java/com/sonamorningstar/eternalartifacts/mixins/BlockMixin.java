package com.sonamorningstar.eternalartifacts.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.sonamorningstar.eternalartifacts.api.ModFakePlayer;
import com.sonamorningstar.eternalartifacts.content.block.entity.BlockBreaker;
import com.sonamorningstar.eternalartifacts.content.block.entity.ChunkEaterBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug
@Mixin(Block.class)
public abstract class BlockMixin {
    
    @WrapWithCondition(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Z)V"))
    private boolean playerDestroy(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool, boolean dropXp) {
        if (entity instanceof ModFakePlayer modFakePlayer) {
            Machine<?> machine = modFakePlayer.getMachine();
            if (machine instanceof BlockBreaker breaker) {
                for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, pos, blockEntity, entity, breaker.inventory.getStackInSlot(0))) {
                    ItemStack remaining = ItemHelper.insertItemStackedForced(breaker.inventory, drop, false, breaker.outputSlots).getFirst();
                    if (!remaining.isEmpty()) {
                        Block.popResource(level, pos, remaining);
                        break;
                    }
                }
                return false;
            } else if (machine instanceof ChunkEaterBlockEntity chunkEater) {
                for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, pos, blockEntity, entity, chunkEater.getInventory(null).getStackInSlot(0))) {
                    ItemStack remaining = ItemHelper.insertItemStackedForced(chunkEater.getInventory(null), drop, false, chunkEater.outputSlots).getFirst();
                    if (!remaining.isEmpty()) {
                        Block.popResource(level, pos, remaining);
                        break;
                    }
                }
                // Handle XP drops directly here by intercepting dropExp? No, Block doesn't drop XP in dropResources.
                return false;
            }
        }
        return true;
    }
}
