package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.item.ToolBlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrowelItem extends TieredItem implements Vanishable {
	private static final long SEED = 2799417252375080941L;
	public TrowelItem(Tier tier, Properties properties) {
		super(tier, properties);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		ItemStack trowel = context.getItemInHand();
		InteractionHand hand = context.getHand();
		if (player != null) {
			List<Integer> blockItemSlots = new ArrayList<>();
			Inventory inventory = player.getInventory();
			for (int i = 0; i < Inventory.getSelectionSize(); i++) {
				ItemStack stack = inventory.getItem(i);
				if (stack.getItem() instanceof BlockItem) {
					blockItemSlots.add(i);
				}
			}
			
			BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
			List<Integer> placeableSlots = blockItemSlots.stream().filter(i -> {
				ItemStack stack = inventory.getItem(i);
				if (stack.getItem() instanceof BlockItem blockItem) {
					ToolBlockPlaceContext placeContext = new ToolBlockPlaceContext(player, hand, stack, placePos, context.getHitResult());
					Block block = blockItem.getBlock();
					BlockState state = block.getStateForPlacement(placeContext);
					return state != null && blockItem.canPlace(placeContext, state);
				}
				return false;
			}).toList();
			int chosenSlot = placeableSlots.isEmpty() ? -1 : placeableSlots.get(getRandomAndUpdateSeed(trowel).nextInt(placeableSlots.size()));
			ItemStack randomStack = chosenSlot == -1 ? ItemStack.EMPTY : inventory.getItem(chosenSlot);
			
			if (!randomStack.isEmpty() && randomStack.getItem() instanceof BlockItem) {
				player.setItemInHand(hand, randomStack);
				
				InteractionResult result;
				result = randomStack.useOn(
					new ToolBlockPlaceContext(player, hand, randomStack, placePos, context.getHitResult())
				);
				
				ItemStack newHandItem = player.getItemInHand(hand);
				player.setItemInHand(hand, trowel);
				inventory.setItem(chosenSlot, newHandItem);
				
				if (result.consumesAction()) {
					trowel.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
					return result;
				}
			}
		}
		return super.useOn(context);
	}
	
	private Random getRandomAndUpdateSeed(ItemStack stack) {
		long seed = SEED;
		Random random = new Random(seed);
		if (stack.hasTag() && stack.getTag().contains("Seed")) {
			seed = stack.getTag().getLong("Seed");
			random = new Random(seed);
			long newSeed = random.nextLong();
			random = new Random(seed);
			stack.getTag().putLong("Seed", newSeed);
		} else {
			if (!stack.hasTag()) {
				stack.setTag(new CompoundTag());
			}
			stack.getTag().putLong("Seed", random.nextLong());
		}
		return random;
	}
}
