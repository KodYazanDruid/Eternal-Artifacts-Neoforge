package com.sonamorningstar.eternalartifacts.api.caches;

import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.*;

/**
    [Source](https://github.com/InnovativeOnlineIndustries/Industrial-Foregoing/blob/1.20/src/main/java/com/buuz135/industrial/utils/apihandlers/plant/TreeCache.java)

    I added tool and block entity parameters for the loot table parameters. I removed shearing thing because loot table rools with given item.
 */
public class TreeCache {

    @Getter
    private Queue<BlockPos> woodCache;
    @Getter
    private Queue<BlockPos> leavesCache;
    private Level level;
    private BlockPos current;
    private ItemStack tool;
    private BlockEntity blockEntity;

    public TreeCache(Level level, BlockPos current, ItemStack tool, BlockEntity blockEntity) {
        this.woodCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distSqr(new Vec3i(((BlockPos) value).getX(), current.getY(), ((BlockPos) value).getZ()))).reversed());
        this.leavesCache = new PriorityQueue<>(Comparator.comparingDouble(value -> ((BlockPos) value).distSqr(new Vec3i(current.getX(), ((BlockPos) value).getY(), current.getZ()))).reversed());
        this.level = level;
        this.current = current;
        this.tool = tool;
        this.blockEntity = blockEntity;
    }

    public List<ItemStack> chop(Queue<BlockPos> cache, @Nullable ServerPlayer player) {
        BlockPos p = cache.peek();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (p == null) return stacks;
        if (BlockHelper.isLeaves(level, p) || BlockHelper.isLog(level, p)) {
            boolean isChopped;
            if (player != null) isChopped = player.gameMode.destroyBlock(p);
            else {
                isChopped = true;
                level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
            }

            if (isChopped) stacks.addAll(BlockHelper.getBlockDrops((ServerLevel) level, p, tool, blockEntity));
            /*if (player != null && hand != null && tool != null && !tool.isEmpty())
                tool.hurtAndBreak(1, player, pl -> pl.broadcastBreakEvent(hand));*/
        }
        cache.poll();
        return stacks;
    }

    public void scanForTreeBlockSection() {
        //Block ogBlock = level.getBlockState(current).getBlock();
        BlockPos highestBlock = getHighestBlock(current);
        for (BlockPos pos : BlockPos.betweenClosed(highestBlock.offset(1, 0, 1), highestBlock.offset(-1, 0, -1))) {
            BlockPos tempHigh = getHighestBlock(pos.immutable());
            if (tempHigh.getY() > highestBlock.getY()) highestBlock = tempHigh;
        }
        highestBlock = highestBlock.offset(0, -Math.min(20, highestBlock.getY() - current.getY()), 0);
        Set<BlockPos> checkedPositions = new HashSet<>();
        Stack<BlockPos> tree = new Stack<>();
        BlockPos test = new BlockPos(current.getX(), highestBlock.getY(), current.getZ());
        //BlockPos test = new BlockPos(current.getX(), current.getY(), current.getZ());
        //for (BlockPos pos : BlockPos.betweenClosed(test.offset(1, 0, 0), test.offset(0, 0, 1))) {
        for (BlockPos pos : BlockPos.betweenClosed(test.offset(1, 1, 1), test.offset(-1, -1, -1))) {
            tree.push(pos.immutable());
        }
        while (!tree.isEmpty()) {
            BlockPos checking = tree.pop();
            if (BlockHelper.isLeaves(level, checking) || BlockHelper.isLog(level, checking)) {
                for (BlockPos pos : BlockPos.betweenClosed(checking.offset(-1, 0, -1), checking.offset(1, 1, 1))) {
                    BlockPos blockPos = pos.immutable();
                    if (level.isEmptyBlock(blockPos) || checkedPositions.contains(blockPos) || blockPos.distManhattan(new Vec3i(current.getX(), current.getY(), current.getZ())) > 100 )
                        continue;
                    if (BlockHelper.isLeaves(level, blockPos)) {
                        tree.push(blockPos);
                        leavesCache.add(blockPos);
                        checkedPositions.add(blockPos);
                    } else if (BlockHelper.isLog(level, blockPos)) {
                        tree.push(blockPos);
                        woodCache.add(blockPos);
                        checkedPositions.add(blockPos);
                    }
                }
            }
        }
    }

    public BlockPos getHighestBlock(BlockPos position) {
        while (!level.isEmptyBlock(position.above()) && (BlockHelper.isLog(level, position.above()) || BlockHelper.isLeaves(level, position.above())))
            position = position.above();
        return position;
    }
}