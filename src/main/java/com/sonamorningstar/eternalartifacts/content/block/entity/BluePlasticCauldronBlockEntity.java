package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.BluePlasticCauldronBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.TickableServer;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class BluePlasticCauldronBlockEntity extends ModBlockEntity implements TickableServer {
    private final int cooldownValue = 200;
    public int cooldown = cooldownValue;

    public BluePlasticCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLUE_PLASTIC_CAULDRON.get(), pos, state);
    }

    public ModItemStorage inventory = new ModItemStorage(1) {
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
             if(thisBe.cooldown == 0) {
                 BlockPos pos = thisBe.getBlockPos();
                 Level level = thisBe.getLevel();
                 BlockState newState = layer == 1 ? Blocks.CAULDRON.defaultBlockState() :
                     ModBlocks.BLUE_PLASTIC_CAULDRON.get().defaultBlockState().setValue(BluePlasticCauldronBlock.LEVEL, layer - 1);
                 if (!simulate) {
                     level.setBlockAndUpdate(pos, newState);
                     level.playSound(null, pos, SoundEvents.ANCIENT_DEBRIS_STEP, SoundSource.BLOCKS, 1.0F, 1.0F);
                     level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                     thisBe.resetCooldown();
                     thisBe.sendUpdate();
                     level.invalidateCapabilities(pos);
                 }
                 return ModItems.PLASTIC_SHEET.toStack();
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
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
