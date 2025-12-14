package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Packer extends GenericMachine {
	public static boolean isPackingMapInitialized = false;
	public static boolean isInitializingPackingMap = false;
	public static Map<Item, Recycler.ItemWithCount> ingredientPacking = new HashMap<>();
	
	public Packer(BlockPos pos, BlockState blockState) {
		super(ModMachines.PACKER, pos, blockState);
		setEnergy(this::createDefaultEnergy);
		outputSlots.add(1);
		setInventory(() -> createBasicInventory(2, (slot, stack) ->
			slot == 0 && ingredientPacking.containsKey(stack.getItem()) ||
				slot == 1 && !outputSlots.contains(slot)));
	}
	
	@Override
	public void onLoad() {
		initializePackingMap(level);
		super.onLoad();
	}
	
	private static void initializePackingMap(Level lvl) {
		if (lvl == null || lvl.isClientSide) return;
		if (isPackingMapInitialized || isInitializingPackingMap) return;
		
		isInitializingPackingMap = true;
		new Thread(() -> {
			try {
				ingredientPacking.clear();
				
				var recs = lvl.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
				for (var r : recs) {
					var recipe = r.value();
					ItemStack result = recipe.getResultItem(lvl.registryAccess());
					if (result == null || result.isEmpty() || result.getCount() != 1) continue;
					
					if (recipe instanceof ShapedRecipe shaped) {
						boolean isMiddleEmpty = shaped.getWidth() == 3 && shaped.getHeight() == 3 &&
							shaped.getIngredients().get(4).isEmpty();
						var ings = shaped.getIngredients().stream().filter(i -> !i.isEmpty()).toList();
						if (ings.isEmpty() || !allSame(ings)) continue;
						
						int w = shaped.getWidth();
						int h = shaped.getHeight();
						int size = ings.size();
						
						if (w == 3 && h == 3 && size == 9) {
							fillMap(ings, result);
						}
						else if (w == 2 && h == 2 && size == 4) {
							fillMap(ings, result);
						}
						else if (w == 3 && h == 3 && size == 8 && isMiddleEmpty) {
							fillMap(ings, result);
						}
					}
					
					else if (recipe instanceof ShapelessRecipe shapeless) {
						var ings = shapeless.getIngredients().stream().filter(i -> !i.isEmpty()).toList();
						if (ings.isEmpty() || !allSame(ings)) continue;
						
						int size = ings.size();
						if (size == 9 || size == 4 || size == 8) {
							fillMap(ings, result);
						}
					}
				}
				
				isPackingMapInitialized = true;
			} finally {
				isInitializingPackingMap = false;
			}
		}).start();
	}
	
	private static boolean allSame(List<Ingredient> ings) {
		if (ings.isEmpty()) return false;
		
		for (ItemStack candidate : ings.get(0).getItems()) {
			boolean allMatch = true;
			for (Ingredient ing : ings) {
				if (ing.isEmpty() || !ing.test(candidate)) {
					allMatch = false;
					break;
				}
			}
			if (allMatch) {
				return true;
			}
		}
		return false;
	}
	
	private static void fillMap(List<Ingredient> ingredients, ItemStack result) {
		if (ingredients.isEmpty()) return;
		
		Ingredient ing = ingredients.get(0);
		for (ItemStack stack : ing.getItems()) {
			if (stack.isEmpty()) continue;
			
			ingredientPacking.putIfAbsent(
				stack.getItem(),
				new Recycler.ItemWithCount(result.getItem(), ingredients.size())
			);
		}
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		ItemStack input = inventory.getStackInSlot(0);
		if (!input.isEmpty() && ingredientPacking.containsKey(input.getItem())) {
			Recycler.ItemWithCount itemWithCount = ingredientPacking.get(input.getItem());
			condition.tryExtractItemForced(itemWithCount.count(), 0);
			ItemStack output = itemWithCount.single();
			if (!output.isEmpty()) condition.queueImport(output);
		}
		condition.commitQueuedImports();
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		initializePackingMap(lvl);
		super.tickServer(lvl, pos, st);
		
		progress(() -> {
			ItemStack input = inventory.getStackInSlot(0);
			Recycler.ItemWithCount itemWithCount = ingredientPacking.get(input.getItem());
			ItemStack unpacked = itemWithCount.single();
			inventory.extractItem(0, itemWithCount.count(), false);
			inventory.insertItemForced(1, unpacked, false);
		});
	}

}
