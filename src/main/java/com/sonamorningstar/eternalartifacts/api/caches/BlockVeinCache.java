package com.sonamorningstar.eternalartifacts.api.caches;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A utility class for vein-mining connected blocks within a specified range.
 * Supports mining multiple types of blocks and block tags.
 * Configurable diameter and search range for the vein.
 * <p>
 * Example usage:
 * <pre>
 *     {@code
 *        BlockVeinCache cache = new BlockVeinCache(Blocks.DIAMOND_ORE, level, pos, 5);
 *        // add mineable blocks and tags
 *        cache.addMineableTag(BlockTags.BASE_STONE_OVERWORLD);
 *        cache.addMineableBlock(Blocks.DIRT);
 *        cache.addMineableBlock(Blocks.GRASS_BLOCK);
 *        cache.scanForBlocks();
 *        // mine a block from the scanned cache
 *        cache.mine(cache.getCache(), player);
 *        // or pass null to ignore drops
 *        cache.mine(cache.getCache(), null);
 *        // or use a loop to mine all blocks
 *        while (!cache.getCache().isEmpty()) {
 *            cache.mine(cache.getCache(), player);
 *        }
 *      }
 * </pre>
 * The above example will mine all blocks connected to the starting position within a 5 block radius.
 */
public class BlockVeinCache {
    @Getter
    private final Queue<BlockPos> cache;
    private final Level level;
    private final BlockPos start;
    private final int rangeX;
    private final int rangeY;
    private final int searchRange;
    private final List<Block> mineableBlocks = new ArrayList<>();
    private final List<TagKey<Block>> mineableTags = new ArrayList<>();
    
    public BlockVeinCache(Block minedBlock, Level level, BlockPos start, int range) {
        this(minedBlock, level, start, range, range, 1);
    }
    
    public BlockVeinCache(Block minedBlock, Level level, BlockPos start, int range, int searchRange) {
        this(minedBlock, level, start, range, range, searchRange);
    }

    public BlockVeinCache(Block minedBlock, Level level, BlockPos start, int rangeX, int rangeY, int searchRange) {
        this.cache = new PriorityQueue<>(Comparator.comparingDouble(value -> value.distSqr(new Vec3i(start.getX(), start.getY(), start.getZ()))));
        this.level = level;
        this.start = start;
        this.rangeX = rangeX;
        this.rangeY = rangeY;
        this.searchRange = searchRange;
        this.mineableBlocks.add(minedBlock);
    }

    public void addMineableTag(TagKey<Block> tag) {
        mineableTags.add(tag);
    }
    public void addMineableBlock(Block block) {
        mineableBlocks.add(block);
    }

    private boolean canMine(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (TagKey<Block> mineableTag : mineableTags) {
            if (state.is(mineableTag)) return true;
        }
        for (Block mineableBlock : mineableBlocks) {
            if (state.is(mineableBlock)) return true;
        }
        return false;
    }

    public void mine(Queue<BlockPos> cache, @Nullable ServerPlayer player) {
        BlockPos pos = cache.peek();
        if (pos == null) return;
        if (canMine(pos)) {
            if (player != null) player.gameMode.destroyBlock(pos);
            else level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        cache.poll();
    }

    public void scanForBlocks() {
        Set<BlockPos> checkedPositions = new HashSet<>();
        Stack<BlockPos> vein = new Stack<>();
        scanArea(start, vein, checkedPositions);
        cache.addAll(vein);
    }

    private void scanArea(BlockPos current, Stack<BlockPos> vein, Set<BlockPos> checked) {
        for (BlockPos pos : BlockPos.betweenClosed(current.offset(searchRange, searchRange, searchRange),
                current.offset(searchRange * -1, searchRange * -1, searchRange * -1))) {
            if (checked.contains(pos)) continue;
            checked.add(pos.immutable());
            if (isInRange(pos) && !vein.contains(pos) && canMine(pos)) {
                vein.add(pos.immutable());
                scanArea(pos, vein, checked);
            }
        }
    }

    private boolean isInRange(BlockPos pos) {
        double x = pos.getX() - start.getX();
        double y = pos.getY() - start.getY();
        double z = pos.getZ() - start.getZ();
        return (x * x) / (rangeX * rangeX) + (y * y) / (rangeY * rangeY) + (z * z) / (rangeX * rangeX) <= 1;
    }
}
