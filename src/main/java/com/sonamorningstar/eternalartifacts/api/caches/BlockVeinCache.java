package com.sonamorningstar.eternalartifacts.api.caches;

import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A utility class for vein-mining connected blocks within a specified range.
 * Supports mining multiple types of blocks and block tags.
 *
 * @param <B> The type of block to be vein-mined, extending {@link Block}.
 */
public class BlockVeinCache {
    @Getter
    private final Queue<BlockPos> cache;
    private final Level level;
    private final BlockPos start;
    private final int rangeX;
    private final int rangeY;
    private ItemStack tool;
    private BlockEntity blockEntity;
    private final List<Block> mineableBlocks = new ArrayList<>();
    private final List<TagKey<Block>> mineableTags = new ArrayList<>();

    public BlockVeinCache(Block minedBlock, Level level, BlockPos start, int range, @Nullable ItemStack tool, @Nullable BlockEntity blockEntity) {
        this(minedBlock, level, start, range, range, tool, blockEntity);
    }

    public BlockVeinCache(Block minedBlock, Level level, BlockPos start, int rangeX, int rangeY, @Nullable ItemStack tool, @Nullable BlockEntity blockEntity) {
        this.cache = new PriorityQueue<>(Comparator.comparingDouble(value -> value.distSqr(new Vec3i(start.getX(), start.getY(), start.getZ()))));
        this.level = level;
        this.start = start;
        this.rangeX = rangeX;
        this.rangeY = rangeY;
        this.tool = tool != null ? tool : ItemStack.EMPTY;
        this.blockEntity = blockEntity;
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

    public List<ItemStack> mine(Queue<BlockPos> cache, @Nullable ServerPlayer player) {
        BlockPos pos = cache.peek();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if (pos == null) return stacks;
        if (canMine(pos)) {
            if (player != null ? player.gameMode.destroyBlock(pos) : level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()))
                stacks.addAll(BlockHelper.getBlockDrops(((ServerLevel) level), pos, tool, blockEntity, player));
        }
        cache.poll();
        return stacks;
    }

    public void scanForBlocks() {
        Set<BlockPos> checkedPositions = new HashSet<>();
        Stack<BlockPos> vein = new Stack<>();
        scanArea(start, vein, checkedPositions);
        cache.addAll(vein);
    }

    private void scanArea(BlockPos current, Stack<BlockPos> vein, Set<BlockPos> checked) {
        for (BlockPos pos : BlockPos.betweenClosed(current.offset(1, 1, 1), current.offset(-1, -1, -1))) {
            if (checked.contains(pos)) continue;
            checked.add(pos.immutable());
            if (!vein.contains(pos) && canMine(pos)) {
                if (isInRange(pos)) scanArea(pos, vein, checked);
                vein.add(pos.immutable());
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
