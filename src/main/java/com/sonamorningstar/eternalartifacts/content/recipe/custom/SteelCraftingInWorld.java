package com.sonamorningstar.eternalartifacts.content.recipe.custom;

import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.*;

public final class SteelCraftingInWorld {

    private static final Item STEEL_CANDIDATE = Items.IRON_INGOT;

    private static final Map<TagKey<Item>, Integer> dustValues = Map.of(
            ModTags.Items.DUSTS_COAL, 2,
            ModTags.Items.DUSTS_CHARCOAL, 4,
            ModTags.Items.DUSTS_SUGAR_CHARCOAL, 8
    );

    public static boolean isInCorrectEnvironment(BlockPos pos, Level level) {
        BlockState blockState = level.getBlockState(pos);
        BlockState belowState = level.getBlockState(pos.below());
        boolean isLit = belowState.is(Blocks.BLAST_FURNACE) && belowState.getValue(BlockStateProperties.LIT);
        return blockState.is(Blocks.CAULDRON) && isLit;
    }

    private static boolean isSteelCandidate(Item item) {
        return Objects.equals(item, STEEL_CANDIDATE);
    }

    public static boolean isValidItem(Item item) {
        for(TagKey<Item> tag : dustValues.keySet()) {
            if(item.getDefaultInstance().is(tag)) return true;
        }
        return isSteelCandidate(item);
    }

    private static int getDustValue(ItemStack stack) {
        List<Map.Entry<TagKey<Item>, Integer>> list = dustValues.entrySet().stream().filter(p -> stack.is(p.getKey())).toList();
        return list.isEmpty() ? 0 : list.get(0).getValue() != null ? list.get(0).getValue() : 0;
    }

    public static void tryTransform(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        AABB area = new AABB(itemEntity.blockPosition());
        List<ItemEntity> entities = level.getEntities(null, area).stream()
                .filter(e -> e instanceof ItemEntity ie && !e.isRemoved() && isValidItem(ie.getItem().getItem())
                ).map(e -> (ItemEntity) e).toList();

        List<ItemStack> cachedItemStacks = new ArrayList<>();

        boolean hasDust = false;
        for (ItemEntity entity : entities) {
            ItemStack stack = entity.getItem();
            if(stack.is(STEEL_CANDIDATE)) cachedItemStacks.add(stack);
            else if(stack.getCount() >= getDustValue(stack) && !hasDust) {
                cachedItemStacks.add(stack);
                hasDust = true;
            }
        }

        if(cachedItemStacks.size() > 1) {
            for(ItemStack stack : cachedItemStacks) {
                if (stack.is(STEEL_CANDIDATE)) stack.shrink(1);
                else stack.shrink(getDustValue(stack));
                if(stack.getCount() < 0) itemEntity.discard();
            }
            ItemEntity steelItemEntity = new ItemEntity(level, itemEntity.xo, itemEntity.yo, itemEntity.zo, ModItems.STEEL_INGOT.toStack());
            level.addFreshEntity(steelItemEntity);
            level.playSound(null, itemEntity.xo, itemEntity.yo, itemEntity.zo, SoundEvents.ANVIL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}