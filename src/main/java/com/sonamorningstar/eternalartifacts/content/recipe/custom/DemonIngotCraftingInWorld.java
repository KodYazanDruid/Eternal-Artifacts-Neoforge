package com.sonamorningstar.eternalartifacts.content.recipe.custom;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class DemonIngotCraftingInWorld {
    private static final Map<Item, ItemStack> candidates = Map.of(
            Items.GOLD_INGOT, ModItems.DEMON_INGOT.toStack(),
            Items.GOLD_BLOCK, ModBlocks.DEMON_BLOCK.toStack()
    );

    public static boolean isInCorrectEnvironment(BlockPos pos, Level level) {
        BlockState blockState = level.getBlockState(pos);
        BlockState nState = level.getBlockState(pos.north());
        BlockState sState = level.getBlockState(pos.south());
        BlockState wState = level.getBlockState(pos.west());
        BlockState eState = level.getBlockState(pos.east());
        return blockState.is(Blocks.LAVA) &&
                nState.is(Blocks.NETHER_BRICKS) &&
                sState.is(Blocks.NETHER_BRICKS) &&
                wState.is(Blocks.NETHER_BRICKS) &&
                eState.is(Blocks.NETHER_BRICKS);
    }

    public static boolean isDemonIngotCandidate(Item item) {
        return candidates.containsKey(item);
    }

    private static ItemStack getStackForCandidate(Item item) {
        return candidates.get(item).copy();
    }

    public static void tryTransform(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        ItemStack stack = itemEntity.getItem();
        ItemEntity demonic = new ItemEntity(level, itemEntity.xo, itemEntity.yo, itemEntity.zo, getStackForCandidate(stack.getItem()));
        stack.shrink(1);
        level.addFreshEntity(demonic);
        level.playSound(null, itemEntity.xo, itemEntity.yo, itemEntity.zo, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (stack.getCount() < 0) itemEntity.discard();
    }
}
