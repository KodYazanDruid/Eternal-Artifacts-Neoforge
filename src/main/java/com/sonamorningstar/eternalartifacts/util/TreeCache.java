package com.sonamorningstar.eternalartifacts.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

/**
    [Source](https://github.com/InnovativeOnlineIndustries/Industrial-Foregoing/blob/1.20/src/main/java/com/buuz135/industrial/utils/apihandlers/plant/TreeCache.java)

    I added tool and block entity parameters for the loot table parameters. I removed shearing thing because loot table rools with given item.
 */
public class TreeCache {

    private Queue<BlockPos> woodCache;
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

    public List<ItemStack> chop(Queue<BlockPos> cache) {
        BlockPos p = cache.peek();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (BlockHelper.isLeaves(level, p) || BlockHelper.isLog(level, p)) {
            stacks.addAll(BlockHelper.getBlockDrops((ServerLevel) level, p, tool, blockEntity));
            level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
        }
        cache.poll();
        return stacks;
    }

    public Queue<BlockPos> getWoodCache() {
        return woodCache;
    }

    public Queue<BlockPos> getLeavesCache() {
        return leavesCache;
    }

    public void scanForTreeBlockSection() {
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