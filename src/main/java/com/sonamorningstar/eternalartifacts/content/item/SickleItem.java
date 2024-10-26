package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.caches.BlockVeinCache;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SickleItem extends DiggerItem {
    public SickleItem(Tier tier, Properties props) {
        super(-1.0F, -2.0F, tier, ModTags.Blocks.MINEABLE_WITH_SICKLE, props);
    }

    private static final List<ItemStack> currentMiners = new ArrayList<>();

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if (isCorrectToolForDrops(stack, state) &&
                living instanceof Player player &&
                !living.isShiftKeyDown() &&
                !currentMiners.contains(stack)) {
            Block block = state.getBlock();
            int range = state.isSolid() ? 3 : 5;
            BlockVeinCache cache = new BlockVeinCache(block, level, pos, range, stack,null);
            cache.addMineableTag(BlockTags.FLOWERS);
            cache.addMineableBlock(Blocks.SHORT_GRASS);
            cache.addMineableBlock(Blocks.TALL_GRASS);
            cache.addMineableBlock(Blocks.FERN);
            cache.addMineableBlock(Blocks.WARPED_ROOTS);
            cache.addMineableBlock(Blocks.WEEPING_VINES);
            cache.addMineableBlock(Blocks.WEEPING_VINES_PLANT);
            cache.addMineableBlock(Blocks.CRIMSON_ROOTS);
            cache.addMineableBlock(Blocks.TWISTING_VINES);
            cache.addMineableBlock(Blocks.TWISTING_VINES_PLANT);
            cache.addMineableBlock(Blocks.FERN);
            cache.addMineableTag(BlockTags.CAVE_VINES);
            cache.scanForBlocks();
            Queue<BlockPos> queuedPos = cache.getCache();
            if (player instanceof ServerPlayer serverPlayer) {
                while (!queuedPos.isEmpty() && !stack.isEmpty()) {
                    if (!currentMiners.contains(stack)) currentMiners.add(stack);
                    cache.mine(queuedPos, serverPlayer);
                }
            }
            currentMiners.remove(stack);
            return false;
        }else return super.mineBlock(stack, level, state, pos, living);
    }

}
