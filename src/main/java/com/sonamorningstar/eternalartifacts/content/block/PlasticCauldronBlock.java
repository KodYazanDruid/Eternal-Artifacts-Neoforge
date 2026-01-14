package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;

import static net.minecraft.world.level.block.Blocks.CAULDRON;

public class PlasticCauldronBlock extends AbstractCauldronBlock {
    public PlasticCauldronBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(CAULDRON), ModCauldronInteraction.PLASTIC);
    }
    
    @Override
    protected MapCodec<? extends AbstractCauldronBlock> codec() {
        return simpleCodec(b -> new PlasticCauldronBlock());
    }
    
    @Override
    public boolean isFull(BlockState pState) {
        return true;
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return Items.CAULDRON.getDefaultInstance();
    }
    
    public static IItemHandler createItemHandler(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, Direction direction) {
        return new ModItemStorage(1) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.is(Tags.Items.DYES_BLUE);
            }
            
            @Override
            public ItemStack insertItemForced(int slot, ItemStack stack, boolean simulate) {
                return insertItem(slot, stack, simulate);
            }
            
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (!stack.isEmpty() && stack.is(Tags.Items.DYES_BLUE) && state.is(ModBlocks.PLASTIC_CAULDRON.get())) {
                    ItemStack remainder = stack.copy();
                    if (!simulate) {
                        level.setBlockAndUpdate(pos, ModBlocks.BLUE_PLASTIC_CAULDRON.get().defaultBlockState()
                            .setValue(BluePlasticCauldronBlock.LEVEL, BluePlasticCauldronBlock.MAX_LEVEL));
                        level.playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                        level.invalidateCapabilities(pos);
                    }
                    remainder.shrink(1);
                    return remainder;
                }
                return stack;
            }
        };
    }
}
