package com.sonamorningstar.eternalartifacts.api.caches;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import lombok.Getter;
import lombok.Setter;
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
 *        BlockVeinCache cache = new BlockVeinCache(level, pos, 5);
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
    private final int startX, startY, startZ;
    private final double rangeXSq, rangeYSq;
    private final long startKey;
    private final int rangeX;
    private final int rangeY;
    private final int searchRange;
    private final Set<Block> mineableBlocks = new HashSet<>();
    private final Set<TagKey<Block>> mineableTags = new HashSet<>();
    @Setter
    private boolean useBFS = false;
    
    private static final int[][] OFFSETS = generateOffsets();
    
    public BlockVeinCache(Level level, BlockPos start, int range) {
        this(level.getBlockState(start).getBlock(), level, start, range, range, 1);
    }
    
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
        this.startX = start.getX();
        this.startY = start.getY();
        this.startZ = start.getZ();
        this.startKey = start.asLong();
        this.rangeXSq = rangeX * rangeX;
        this.rangeYSq = rangeY * rangeY;
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
        for (Block mineableBlock : mineableBlocks) {
            if (state.is(mineableBlock)) return true;
        }
        for (TagKey<Block> mineableTag : mineableTags) {
            if (state.is(mineableTag)) return true;
        }
        return false;
    }
    private boolean canMine(long posKey) {
        return canMine(BlockPos.of(posKey));
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
        if (useBFS || searchRange == 1) {
            scanBFS();
        } else {
            Set<BlockPos> checkedPositions = new HashSet<>();
            Stack<BlockPos> vein = new Stack<>();
            scanDFS(start, vein, checkedPositions);
            cache.addAll(vein);
        }
    }
    
    private static int[][] generateOffsets() {
        int[][] dirs = new int[26][3];
        int i = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        dirs[i][0] = x;
                        dirs[i][1] = y;
                        dirs[i][2] = z;
                        i++;
                    }
                }
            }
        }
        return dirs;
    }
    
    public void scanBFS() {
        LongOpenHashSet visited = new LongOpenHashSet();
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
        
        if (canMine(startKey)) {
            queue.enqueue(startKey);
            visited.add(startKey);
            cache.add(BlockPos.of(startKey));
        }
        
        while (!queue.isEmpty()) {
            long currentKey = queue.dequeueLong();
            int cx = BlockPos.getX(currentKey);
            int cy = BlockPos.getY(currentKey);
            int cz = BlockPos.getZ(currentKey);
            
            for (int[] dir : OFFSETS) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];
                int nz = cz + dir[2];
                long neighborKey = BlockPos.asLong(nx, ny, nz);
                
                if (!visited.contains(neighborKey)) {
                    visited.add(neighborKey);
                    
                    if (isInRange(nx, ny, nz) && canMine(neighborKey)) {
                        queue.enqueue(neighborKey);
                        cache.add(BlockPos.of(neighborKey));
                    }
                }
            }
        }
    }

    private void scanDFS(BlockPos current, Stack<BlockPos> vein, Set<BlockPos> checked) {
        for (BlockPos pos : BlockPos.betweenClosed(current.offset(searchRange, searchRange, searchRange),
                current.offset(searchRange * -1, searchRange * -1, searchRange * -1))) {
            if (checked.contains(pos)) continue;
            checked.add(pos.immutable());
            if (isInRange(pos) && !vein.contains(pos) && canMine(pos)) {
                vein.add(pos.immutable());
                scanDFS(pos, vein, checked);
            }
        }
    }

    private boolean isInRange(BlockPos pos) {
        double dx = pos.getX() - startX;
        double dy = pos.getY() - startY;
        double dz = pos.getZ() - startZ;
        return (dx * dx) / rangeXSq + (dy * dy) / rangeYSq + (dz * dz) / rangeXSq <= 1.0;
    }
    private boolean isInRange(int x, int y, int z) {
        double dx = x - startX;
        double dy = y - startY;
        double dz = z - startZ;
        return (dx * dx) / rangeXSq + (dy * dy) / rangeYSq + (dz * dz) / rangeXSq <= 1.0;
    }
}
