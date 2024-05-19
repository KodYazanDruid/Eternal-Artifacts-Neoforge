package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.util.AutomationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.Collections;
import java.util.List;

public class GardeningPotEntity extends DefaultRetexturedBlockEntity{

    public GardeningPotEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GARDENING_POT.get(), pPos, pBlockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide || !(level instanceof ServerLevel)) return;
        IItemHandler inventoryBelow = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);
        BlockState plantState = level.getBlockState(pos.above());
        if(plantState.getBlock() instanceof SugarCaneBlock ||
                plantState.getBlock() instanceof CactusBlock ||
                plantState.getBlock() instanceof BambooStalkBlock) {

            BlockPos toBreak = pos.above(2);
            List<ItemStack> drops = Collections.emptyList();
            if (level.getBlockState(toBreak).is(plantState.getBlock())) drops = AutomationHelper.doReedlikeHarvest((ServerLevel) level, toBreak);
            pushOrPop(drops, level, pos.above(), inventoryBelow);
        }
        /*else if(BlockHelper.isLog(level, pos.above())){
            List<ItemStack> drops = AutomationHelper.doTreeHarvest(level, pos.above(), ItemStack.EMPTY, this);
            if(!drops.isEmpty()){
                boolean saplingSet = false;
                ItemStack sapling = ItemStack.EMPTY;
                for (int i = 0; i < drops.size(); i++) {
                    ItemStack stack = drops.get(i);
                    if (!saplingSet && stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SaplingBlock) {
                        sapling = stack.copyWithCount(1);
                        saplingSet = true;
                        drops.set(i, stack.copyWithCount(stack.getCount() - 1));
                    }
                }
                if (sapling.getItem() instanceof BlockItem saplingBlock) {
                    level.setBlockAndUpdate(pos.above(), saplingBlock.getBlock().defaultBlockState());
                }
                pushOrPop(drops, level, pos.above(), inventoryBelow);
                level.sendBlockUpdated(pos, state, state, 2);
            }
        }*/

    }

    private void pushOrPop(List<ItemStack> resources, Level level, BlockPos pos, IItemHandler inventory) {
        for(ItemStack stack : resources) {
            ItemStack remainder = stack;

            if (!remainder.isEmpty()) {
                remainder = ItemHandlerHelper.insertItemStacked(inventory, remainder, false);
            }
            if(!remainder.isEmpty() && !level.isClientSide()) {
                Block.popResource(level, pos.above(), remainder);
            }
        }
    }

}
