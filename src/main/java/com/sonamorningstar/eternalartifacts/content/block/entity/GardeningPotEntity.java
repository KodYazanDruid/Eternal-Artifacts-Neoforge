package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.ModFakePlayer;
import com.sonamorningstar.eternalartifacts.api.farm.FarmBehavior;
import com.sonamorningstar.eternalartifacts.api.farm.FarmBehaviorRegistry;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.DefaultRetexturedBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

public class GardeningPotEntity extends DefaultRetexturedBlockEntity {

    public GardeningPotEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GARDENING_POT.get(), pPos, pBlockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }
        IItemHandler inventoryBelow = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);
        BlockPos targetPos = pos.above();
        
        FarmBehavior behavior = FarmBehaviorRegistry.get(level, targetPos);
        if (behavior != null && behavior.canHarvest(level, targetPos)) {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, targetPos, level.getBlockState(targetPos), FakePlayerHelper.getFakePlayer(level));
            if (event.isCanceled()) return;
            
            List<ItemStack> drops = behavior.harvest(level, targetPos, null, null);
            
            boolean planted = false;
            for (ItemStack drop : drops) {
                if (!drop.isEmpty()) {
                    if (!planted && behavior.isCorrectSeed(drop) && behavior.supportsReplanting()) {
                        FarmBehavior.PlantResult plantResult = behavior.getReplantingState(level, targetPos, drop);
                        BlockState plantState = plantResult.state();
                        BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, targetPos);
                        boolean cancelled = EventHooks.onBlockPlace(FakePlayerHelper.getFakePlayer(level), snapshot, plantResult.facing());
                        if (cancelled) {
                            level.restoringBlockSnapshots = true;
                            snapshot.restore(true, false);
                            level.restoringBlockSnapshots = false;
                            continue;
                        }
                        if (!plantState.isAir()) {
                            level.playSound(null, targetPos, behavior.getReplantSound(plantState), SoundSource.BLOCKS);
                            level.setBlockAndUpdate(targetPos, plantState);
                            level.gameEvent(null, GameEvent.BLOCK_CHANGE, targetPos);
                            drop.shrink(1);
                            planted = true;
                        }
                    }
                }
            }
            pushOrPop(drops, level, targetPos, inventoryBelow);
        }
    }

    private void pushOrPop(List<ItemStack> resources, Level level, BlockPos pos, @Nullable IItemHandler inventory) {
        for(ItemStack stack : resources) {
            ItemStack remainder = stack;

            if (!remainder.isEmpty() && inventory != null) {
                remainder = ItemHandlerHelper.insertItemStacked(inventory, remainder, false);
            }
            if(!remainder.isEmpty() && !level.isClientSide()) {
                Block.popResource(level, pos.above(), remainder);
            }
        }
    }

}
