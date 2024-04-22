package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.PlantHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

public class GardeningPotEntity extends DefaultRetexturedBlockEntity{

    public GardeningPotEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GARDENING_POT.get(), pPos, pBlockState);
    }

    //Needs chopping delay.
    //Sapling may not drop.
    public void tick(Level level, BlockPos pos, BlockState state) {
        if(level.isClientSide || !(level instanceof ServerLevel)) return;
        IItemHandler inventoryBelow = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);
        if(level.getBlockState(pos.above()).getBlock() instanceof SugarCaneBlock ||
                level.getBlockState(pos.above()).getBlock() instanceof CactusBlock ||
                level.getBlockState(pos.above()).getBlock() instanceof BambooStalkBlock) {

            List<ItemStack> drops = PlantHelper.doReedlikeHarvest((ServerLevel) level, pos.above(2));
            pushOrPop(drops, level, pos.above(), inventoryBelow);
        }
        /*else if(BlockHelper.isLog(level, pos.above())){
            List<ItemStack> drops = PlantHelper.doTreeHarvest(level, pos.above(), ItemStack.EMPTY, this);
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
