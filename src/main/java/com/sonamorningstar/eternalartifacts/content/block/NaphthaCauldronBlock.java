package com.sonamorningstar.eternalartifacts.content.block;

import com.mojang.serialization.MapCodec;
import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.base.ModLayeredCauldronBlack;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;

public class NaphthaCauldronBlock extends ModLayeredCauldronBlack {
	public static final int MIN_LEVEL = 1;
	public static final int MAX_LEVEL = 4;
	public static final IntegerProperty LEVEL = IntegerProperty.create("level", MIN_LEVEL, MAX_LEVEL);
	
	public NaphthaCauldronBlock(Properties props) {
		super(ModCauldronInteraction.NAPHTHA, props);
	}
	
	@Override
	public IntegerProperty getLevelProperty() { return LEVEL; }
	
	@Override
	public int getMinLevel() { return MIN_LEVEL; }
	
	@Override
	public int getMaxLevel() { return MAX_LEVEL; }
	
	@Override
	protected MapCodec<? extends ModLayeredCauldronBlack> codec() {
		return simpleCodec(NaphthaCauldronBlock::new);
	}
	
	public static IItemHandler createItemHandler(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity, Direction direction) {
		if (state.getValue(LEVEL) == MAX_LEVEL) {
			return new ModItemStorage(1) {
				@Override
				public boolean isItemValid(int slot, ItemStack stack) {
					return !stack.isEmpty() && stack.is(Tags.Items.SAND) && stack.getCount() >= 16;
				}
				
				@Override
				public ItemStack insertItemForced(int slot, ItemStack stack, boolean simulate) {
					return insertItem(slot, stack, simulate);
				}
				
				@Override
				public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
					if (state.getValue(LEVEL) != MAX_LEVEL) {
						return stack;
					}
					if (stack.isEmpty()) {
						return ItemStack.EMPTY;
					}
					if (!isItemValid(slot, stack)) {
						return stack;
					}
					ItemStack remainder = stack.copy();
					remainder.shrink(16);
					if (!simulate) {
						level.setBlockAndUpdate(pos, ModBlocks.PLASTIC_CAULDRON.get().defaultBlockState());
						level.playSound(null, pos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 1.0F, 1.0F);
						level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
						level.invalidateCapabilities(pos);
					}
					return remainder;
				}
			};
		}
		return null;
	}
}
