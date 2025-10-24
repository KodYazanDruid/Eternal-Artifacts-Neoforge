package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.ItemDynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;

public class SolidCombustionDynamo extends AbstractDynamo<ItemDynamoMenu> {
	private ItemStack burnable = ItemStack.EMPTY;
	public SolidCombustionDynamo(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.SOLID_COMBUSTION_DYNAMO.get(), pos, blockState, ItemDynamoMenu::new);
		setInventory(() -> createRecipeFinderInventory(1, (i, s) -> true));
		setRecipeTypeAndContainer(ModRecipes.SOLID_COMBUSTING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0)));
	}
	
	@Override
	protected void findRecipe() {
		if (cache != null) return;
		super.findRecipe();
		if (RecipeCache.getCachedRecipe(this) == null) {
			ItemStack stack = inventory.getStackInSlot(0);
			int burnTime = stack.getBurnTime(ModRecipes.SOLID_COMBUSTING.getType());
			if (burnTime > 0) {
				burnable = stack;
				int celerity = getEnchantmentLevel(ModEnchantments.CELERITY.get());
				setEnergyPerTick(defaultEnergyPerTick * ((celerity / 3) + 1));
				int eff = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
				setMaxProgress(burnTime * ((eff / 5) + 1));
			} else burnable = ItemStack.EMPTY;
		} else burnable = ItemStack.EMPTY;
	}
	
	@Override
	protected boolean canProcessRecipeless() {
		return !burnable.isEmpty();
	}
	
	@Override
	protected void executeRecipe(Recipe<?> recipe, QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		if (!inventory.getStackInSlot(0).isEmpty()) {
			inventory.extractItem(0, 1, false);
			cacheGetter.apply(maxProgress, energy, energyPerTick, this);
		}
	}
	
	@Override
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		if (!burnable.isEmpty() && burnable.getBurnTime(ModRecipes.SOLID_COMBUSTING.getType()) > 0) {
			if (burnable.hasCraftingRemainingItem()) inventory.setStackInSlot(0, burnable.getCraftingRemainingItem());
			else if (!burnable.isEmpty()){
				burnable.shrink(1);
				if (burnable.isEmpty()) inventory.setStackInSlot(0, burnable.getCraftingRemainingItem());
			}
			cacheGetter.apply(maxProgress, energy, energyPerTick, this);
		}
	}
}
