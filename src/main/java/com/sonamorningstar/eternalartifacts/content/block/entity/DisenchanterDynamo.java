package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.ItemDynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DisenchanterDynamo extends AbstractDynamo<ItemDynamoMenu> {
	public DisenchanterDynamo(BlockPos pos, BlockState blockState) {
		super(ModMachines.DISENCHANTER_DYNAMO, pos, blockState);
		setInventory(() -> createBasicInventory(1, (slot, stack) -> canProcessItem(stack), slot -> 1));
		setDefaultEnergyPerTick(60);
	}
	
	@Override
	protected boolean canProcessRecipeless() {
		return true;
	}
	
	private boolean canProcessItem(ItemStack stack) {
		if (stack.is(Items.ENCHANTED_BOOK)) return true;
		if (!stack.isEmpty() && stack.isEnchanted()) {
			Map<Enchantment, Integer> nonCurses = getNonCurseEnchantments(stack);
			return !nonCurses.isEmpty();
		}
		return false;
	}
	
	private Map<Enchantment, Integer> getNonCurseEnchantments(ItemStack stack) {
		return EnchantmentHelper.getEnchantments(stack)
			.entrySet()
			.stream()
			.filter(entry -> !entry.getKey().isCurse())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	private ItemStack removeEnchantments(ItemStack stack) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		
		if (enchantments.isEmpty()) {
			return stack.copy();
		}
		
		Map<Enchantment, Integer> remaining = new HashMap<>();
		
		for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			if (entry.getKey().isCurse()) {
				remaining.put(entry.getKey(), entry.getValue());
			}
		}
		
		ItemStack result;
		
		if (stack.is(Items.ENCHANTED_BOOK)) {
			if (remaining.isEmpty()) {
				result = new ItemStack(Items.BOOK);
			} else {
				result = new ItemStack(Items.ENCHANTED_BOOK);
				EnchantmentHelper.setEnchantments(remaining, result);
			}
		} else {
			result = stack.copy();
			
			if (remaining.isEmpty()) {
				result.removeTagKey("Enchantments");
			} else {
				EnchantmentHelper.setEnchantments(remaining, result);
			}
		}
		
		return result;
	}
	
	@Override
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		ItemStack stack = inventory.getStackInSlot(0);
		if (canProcessItem(stack)) {
			Map<Enchantment, Integer> nonCurseEnchantments = getNonCurseEnchantments(stack);
			int totalDuration = 0;
			int generation = 0;
			for (Map.Entry<Enchantment, Integer> entry : nonCurseEnchantments.entrySet()) {
				totalDuration += entry.getKey().getMinCost(entry.getValue());
				generation += entry.getValue() * (defaultEnergyPerTick);
			}
			int celerity = getEnchantmentLevel(ModEnchantments.CELERITY.get());
			int efficiency = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
			setMaxProgress(totalDuration * ((efficiency / 5) + 1));
			setEnergyPerTick(generation * ((celerity / 3) + 1));
			inventory.setStackInSlot(0, removeEnchantments(stack));
			cacheGetter.apply(maxProgress, energy, energyPerTick, this);
		}
	}
}
