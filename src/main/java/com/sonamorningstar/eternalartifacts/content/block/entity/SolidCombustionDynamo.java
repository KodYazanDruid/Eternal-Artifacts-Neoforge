package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.block_search.DynamoProcessCache;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.ItemDynamoMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractDynamo;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.function.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;

public class SolidCombustionDynamo extends AbstractDynamo<ItemDynamoMenu> {
	public SolidCombustionDynamo(BlockPos pos, BlockState blockState) {
		super(ModMachines.SOLID_COMBUSTION_DYNAMO, pos, blockState);
		setInventory(() -> createRecipeFinderInventory(1, (i, s) -> true));
		setRecipeTypeAndContainer(ModRecipes.SOLID_COMBUSTING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0)));
	}
	
	@Override
	public void findRecipe() {
		if (cache != null) return;
		super.findRecipe();
	}
	
	@Override
	protected boolean canProcessRecipeless() {
		return inventory.getStackInSlot(0).getBurnTime(ModRecipes.SOLID_COMBUSTING.getType()) > 0;
	}
	
	@Override
	protected void executeRecipe(Recipe<?> recipe, QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		if (!inventory.getStackInSlot(0).isEmpty()) {
			inventory.extractItem(0, 1, false);
			prepareDynamoEnergyAndDuration();
			cacheGetter.apply(maxProgress, energy, energyPerTick, this);
		}
	}
	
	@Override
	protected void executeRecipeless(QuadFunction<Integer, ModEnergyStorage, Integer, AbstractDynamo<?>, DynamoProcessCache> cacheGetter) {
		ItemStack stack = inventory.getStackInSlot(0);
		int burnTime = stack.getBurnTime(ModRecipes.SOLID_COMBUSTING.getType());
		if (burnTime > 0) {
			defaultMaxProgress = burnTime;
			prepareDynamoEnergyAndDuration();
			inventory.extractItem(0, 1, false);
			if (stack.hasCraftingRemainingItem()) {
				ItemStack remainder = stack.getCraftingRemainingItem();
				if (!remainder.isEmpty()) inventory.setStackInSlot(0, remainder);
			}
			cacheGetter.apply(maxProgress, energy, energyPerTick, this);
		}
	}
}
