package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.util.FakePlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Smithinator extends GenericMachine {
	private List<RecipeHolder<SmithingRecipe>> recipes;
	public Smithinator(BlockPos pos, BlockState blockState) {
		super(ModMachines.SMITHINATOR, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		outputSlots.add(3);
		screenInfo.setArrowXOffset(20);
		setInventory(() -> createRecipeFinderInventory(4, this::checkSlot));
		setRecipeTypeAndContainer(RecipeType.SMITHING,
			() -> new SimpleContainer(inventory.getStackInSlot(0), inventory.getStackInSlot(1), inventory.getStackInSlot(2))
		);
		this.recipes = level != null ? level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING) : List.of();
	}
	
	private boolean checkSlot(int slot, ItemStack stack) {
		return recipes != null ? switch (slot) {
			case 0 -> recipes.stream().anyMatch(holder -> holder.value().isTemplateIngredient(stack));
			case 1 -> recipes.stream().anyMatch(holder -> holder.value().isBaseIngredient(stack));
			case 2 -> recipes.stream().anyMatch(holder -> holder.value().isAdditionIngredient(stack));
			default -> !outputSlots.contains(slot);
		} : !outputSlots.contains(slot);
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		this.recipes = level != null ? level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING) : List.of();
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		if (recipe instanceof SmithingRecipe) {
			condition
				.queueImport(recipe.getResultItem(level.registryAccess()))
				.commitQueuedImports();
		}
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		super.tickServer(lvl, pos, st);
		SmithingRecipe recipe = (SmithingRecipe) RecipeCache.getCachedRecipe(this);
		if (recipe == null) {
			progress = 0;
			return;
		}
		progress(() -> {
			ItemStack stack = recipe.assemble(recipeContainer.get(), lvl.registryAccess());
			FakePlayer fakePlayer = FakePlayerHelper.getFakePlayer(this, lvl);
			stack.onCraftedBy(lvl, fakePlayer, 1);
			inventory.extractItem(0, 1, false);
			inventory.extractItem(1, 1, false);
			inventory.extractItem(2, 1, false);
			inventory.insertItemForced(3, stack, false);
		});
	}
}
