package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LifterItem extends Item {
	public static final String TAG_BLOCK_ENTITY = "StoredBlockEntity";
	public static final String TAG_BLOCK_STATE = "StoredBlockState";
	public static final String TAG_BLOCK_ID = "StoredBlockId";
	
	public LifterItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		if (player == null) return InteractionResult.PASS;
		
		InteractionHand hand = context.getHand();
		ItemStack stack = player.getItemInHand(hand);
		BlockPos clickedPos = context.getClickedPos();
		Direction clickedFace = context.getClickedFace();
		
		if (hasStoredBlockEntity(stack)) {
			BlockPos placePos = clickedPos.relative(clickedFace);
			if (level.isClientSide()) return InteractionResult.SUCCESS;
			return placeBlockEntity(level, placePos, stack, player);
		} else {
			if (level.isClientSide()) return InteractionResult.SUCCESS;
			return pickupBlockEntity(level, clickedPos, stack, player);
		}
	}
	
	private InteractionResult pickupBlockEntity(Level level, BlockPos pos, ItemStack stack, Player player) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null) return InteractionResult.FAIL;
		
		BlockState state = level.getBlockState(pos);
		if (state.is(ModTags.Blocks.LIFTER_BLACKLISTED)) return InteractionResult.FAIL;
		CompoundTag tag = stack.getOrCreateTag();
		
		CompoundTag beTag = be.saveWithoutMetadata();
		tag.put(TAG_BLOCK_ENTITY, beTag);
		tag.putInt(TAG_BLOCK_STATE, Block.getId(state));
		tag.putString(TAG_BLOCK_ID, state.getBlock().getDescriptionId());
		
		level.removeBlockEntity(pos);
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
		
		return InteractionResult.CONSUME;
	}
	
	private InteractionResult placeBlockEntity(Level level, BlockPos pos, ItemStack stack, Player player) {
		if (!level.getBlockState(pos).canBeReplaced()) return InteractionResult.FAIL;
		
		CompoundTag tag = stack.getTag();
		if (tag == null) return InteractionResult.FAIL;
		
		int stateId = tag.getInt(TAG_BLOCK_STATE);
		BlockState state = Block.stateById(stateId);
		if (state.isAir()) return InteractionResult.FAIL;
		
		state = Block.updateFromNeighbourShapes(state, level, pos);
		
		FluidState fluidState = level.getFluidState(pos);
		if (!fluidState.isEmpty() && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
			state = state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
		}
		
		level.setBlock(pos, state, Block.UPDATE_ALL);
		
		BlockEntity be = level.getBlockEntity(pos);
		if (be != null && tag.contains(TAG_BLOCK_ENTITY)) {
			CompoundTag beTag = tag.getCompound(TAG_BLOCK_ENTITY);
			be.load(beTag);
			be.setChanged();
		}
		
		state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
		
		tag.remove(TAG_BLOCK_ENTITY);
		tag.remove(TAG_BLOCK_STATE);
		tag.remove(TAG_BLOCK_ID);
		if (tag.isEmpty()) stack.setTag(null);
		
		return InteractionResult.CONSUME;
	}
	
	public static boolean hasStoredBlockEntity(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(TAG_BLOCK_ENTITY);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return hasStoredBlockEntity(stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		if (hasStoredBlockEntity(stack)) {
			CompoundTag tag = stack.getTag();
			if (tag != null && tag.contains(TAG_BLOCK_ID)) {
				String blockId = tag.getString(TAG_BLOCK_ID);
				tooltip.add(ModConstants.TOOLTIP.withSuffixTranslatable("lifter.stored", Component.translatable(blockId)).withStyle(ChatFormatting.GRAY));
			}
		}
	}
}
