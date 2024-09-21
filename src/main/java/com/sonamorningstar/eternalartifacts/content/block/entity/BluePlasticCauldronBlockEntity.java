package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.BluePlasticCauldronBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ITickableServer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BluePlasticCauldronBlockEntity extends ModBlockEntity implements ITickableServer {
    private final int cooldownValue = 200;
    public int cooldown = cooldownValue;

    public BluePlasticCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLUE_PLASTIC_CAULDRON.get(), pos, state);
    }

    public ModItemStorage inventory = new ModItemStorage(1) {
        @Override
        protected void onContentsChanged(int slot) {
            BluePlasticCauldronBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0) return ItemStack.EMPTY;
            validateSlotIndex(slot);
            BluePlasticCauldronBlockEntity thisBe = BluePlasticCauldronBlockEntity.this;
            int layer = thisBe.getBlockState().getValue(BluePlasticCauldronBlock.LEVEL);
            if (layer <= BluePlasticCauldronBlock.MAX_FILL_LEVEL && layer > 0) {
                 if(thisBe.cooldown == 0) {
                     BlockPos pos = thisBe.getBlockPos();
                     Level level = thisBe.getLevel();
                     if(layer == BluePlasticCauldronBlock.MIN_FILL_LEVEL) {
                         if(level != null) {
                             if(!simulate){
                                 level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                                 //I am setting state to different block. IDK if these functions do anything lol.
                                 thisBe.resetCooldown();
                                 thisBe.sendUpdate();
                             }
                             return ModItems.PLASTIC_SHEET.toStack();
                         }
                     }else {
                         if(level != null) {
                             if(!simulate){
                                 level.setBlockAndUpdate(pos, ModBlocks.BLUE_PLASTIC_CAULDRON.get().defaultBlockState().setValue(BluePlasticCauldronBlock.LEVEL, layer - 1));
                                 thisBe.resetCooldown();
                                 thisBe.sendUpdate();
                             }
                             return ModItems.PLASTIC_SHEET.toStack();
                         }
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {return ItemStack.EMPTY;}
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Cooldown", cooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        cooldown = tag.getInt("Cooldown");
    }

    @Override
    protected boolean shouldSyncOnUpdate() { return true; }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        if(cooldown == 0) return;
        BlockState state = lvl.getBlockState(pos);
        if(state.getBlock() instanceof BluePlasticCauldronBlock) {
            int layer = state.getValue(BluePlasticCauldronBlock.LEVEL);
            if (layer >= 1 && cooldown > 0) {
                cooldown--;
                sendUpdate();
            }
        }
    }

    public void resetCooldown() {
        cooldown = cooldownValue;
    }
}
