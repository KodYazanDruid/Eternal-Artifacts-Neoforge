package com.sonamorningstar.eternalartifacts.api.caches;

import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.*;

public class OreCache {
    @Getter
    private Queue<BlockPos> oreCache;
    private Level level;
    private BlockPos current;
    private ItemStack tool;
    private BlockEntity blockEntity;
    private Block minedOre;

    public OreCache(Level level, BlockPos current, ItemStack tool, BlockEntity blockEntity) {
        //this.oreCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distSqr(new Vec3i(((BlockPos) value).getX(), current.getY(), ((BlockPos) value).getZ()))).reversed());
        this.oreCache = new PriorityQueue<>(Comparator.comparingDouble(value -> value.distSqr(new Vec3i(current.getX(), current.getY(), current.getZ()))));
        this.level = level;
        this.current = current;
        this.tool = tool;
        this.blockEntity = blockEntity;
        this.minedOre = level.getBlockState(current).getBlock();
    }

    public List<ItemStack> mine(Queue<BlockPos> cache, @Nullable ServerPlayer player) {
        BlockPos p = cache.peek();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (p == null) return stacks;
        if (BlockHelper.isOre(level, p)) {
            boolean isMined;
            if (player != null) isMined = player.gameMode.destroyBlock(p);
            else {
                isMined = true;
                level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
            }

            if (isMined) stacks.addAll(BlockHelper.getBlockDrops((ServerLevel) level, p, tool, blockEntity, player));
            /*if (player != null && hand != null && tool != null && !tool.isEmpty())
                tool.hurtAndBreak(1, player, pl -> pl.broadcastBreakEvent(hand));*/
        }
        cache.poll();
        return stacks;
    }

    public void scanForOreVein() {
        Set<BlockPos> checkedPositions = new HashSet<>();
        Stack<BlockPos> oreVein = new Stack<>();
        scanArea(current, oreVein, checkedPositions);

        oreCache.addAll(oreVein);
    }

    private void scanArea(BlockPos main, Stack<BlockPos> vein, Set<BlockPos> checked) {
        for(BlockPos pos : BlockPos.betweenClosed(main.offset(1, 1, 1), main.offset(-1, -1, -1))) {
            if(checked.contains(pos)) continue;
            checked.add(pos.immutable());
            if(!vein.contains(pos) && BlockHelper.isSame(level, pos, minedOre)) {
                if (pos.distSqr(new Vec3i(current.getX(), current.getY(), current.getZ())) <= 10) scanArea(pos, vein, checked);
                vein.add(pos.immutable());
            }
        }
    }

}
