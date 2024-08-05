package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.caches.OreCache;
import com.sonamorningstar.eternalartifacts.caches.TreeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.*;

public class AutomationHelper {

    /**
     * @param level The level that harvesting happens.
     * @param pos The position that harvesting starts.
     * @return ItemStack list after harvest.
     */
    public static List<ItemStack> doReedlikeHarvest(ServerLevel level, BlockPos pos) {
        int baseY = pos.getY();
        while(level.getBlockState(pos.above()).getBlock() instanceof SugarCaneBlock ||
              level.getBlockState(pos.above()).getBlock() instanceof CactusBlock ||
              level.getBlockState(pos.above()).getBlock() instanceof BambooStalkBlock) {

            pos = pos.above();
        }
        NonNullList<ItemStack> totalDrops = NonNullList.create();
        while (pos.getY() >= baseY) {
            totalDrops.addAll(BlockHelper.getBlockDrops(level, pos, null, null));
            level.destroyBlock(pos, false);
            pos = pos.below();
        }
        return totalDrops;
    }

    public static List<ItemStack> doTreeHarvest(Level level, BlockPos pos, @Nullable ItemStack axe, @Nullable BlockEntity blockEntity) {
        TreeCache cache = new TreeCache(level, pos, axe, blockEntity);
        cache.scanForTreeBlockSection();

        NonNullList<ItemStack> totalDrops = NonNullList.create();

        while(!cache.getLeavesCache().isEmpty() || !cache.getWoodCache().isEmpty()) {
            if(!cache.getLeavesCache().isEmpty()) totalDrops.addAll(cache.chop(cache.getLeavesCache()));
            else totalDrops.addAll(cache.chop(cache.getWoodCache()));
        }

        return totalDrops;
    }

    public static List<ItemStack> doOreVeinMine(Level level, BlockPos pos, @Nullable ItemStack pickaxe, @Nullable BlockEntity blockEntity) {
        OreCache cache = new OreCache(level, pos, pickaxe, blockEntity);
        cache.scanForOreVein();

        NonNullList<ItemStack> totalDrops = NonNullList.create();

        while(!cache.getOreCache().isEmpty()) {
            totalDrops.addAll(cache.mine(cache.getOreCache()));
        }

        return totalDrops;
    }

}
