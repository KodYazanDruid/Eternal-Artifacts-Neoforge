package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.container.AutoCutterMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class AutoCutter extends SidedTransferMachine<AutoCutterMenu> {
	private int selectedRecipeIndex = -1;
	private List<RecipeHolder<Recipe<Container>>> recipes = List.of();
	
	public AutoCutter(BlockPos pos, BlockState blockState) {
		super(ModMachines.AUTOCUTTER.getBlockEntity(), pos, blockState, (a, b, c, d) ->
			new AutoCutterMenu(ModMachines.AUTOCUTTER.getMenu(), a, b, c, d));
		setEnergy(this::createDefaultEnergy);
		outputSlots.add(1);
		setInventory(() -> createRecipeFinderInventory(2, outputSlots));
		setRecipeTypeAndContainer(RecipeType.STONECUTTING, () -> new SimpleContainer(inventory.getStackInSlot(0)));
	}
	
	@Override
	protected ContainerData createContainerData() {
		return new ContainerData() {
			@Override
			public int get(int index) {
				return switch (index) {
					case 0 -> progress;
					case 1 -> maxProgress;
					case 2 -> selectedRecipeIndex;
					default -> throw new IllegalStateException("Unexpected value: " + index);
				};
			}
			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> progress = value;
					case 1 -> maxProgress = value;
					case 2 -> selectedRecipeIndex = value;
				}
			}
			@Override
			public int getCount() {
				return 3;
			}
		};
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		selectedRecipeIndex = tag.getInt("SelectedRecipeIndex");
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("SelectedRecipeIndex", selectedRecipeIndex);
	}
	
	@Override
	public void loadContents(CompoundTag additionalTag) {
		super.loadContents(additionalTag);
		selectedRecipeIndex = additionalTag.getInt("SelectedRecipeIndex");
	}
	
	@Override
	public void saveContents(CompoundTag additionalTag) {
		super.saveContents(additionalTag);
		additionalTag.putInt("SelectedRecipeIndex", selectedRecipeIndex);
	}
	
	@Override
	protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
		if (selectedRecipeIndex >= 0 && !recipes.isEmpty() && selectedRecipeIndex < recipes.size()) {
			StonecutterRecipe cuttingRecipe = (StonecutterRecipe) recipes.get(selectedRecipeIndex).value();
			condition.queueImport(cuttingRecipe.getResultItem(level.registryAccess())).commitQueuedImports();
		}
		super.setProcessCondition(condition, recipe);
	}
	
	@Override
	protected void findRecipe() {
		recipes = level.getRecipeManager()
			.getRecipesFor((RecipeType<Recipe<Container>>) recipeType, recipeContainer.get(), level);
	}
	
	public void setSelectedRecipeIndex(int index) {
		if (index < recipes.size()) {
			selectedRecipeIndex = index;
			setProcessCondition(new ProcessCondition(this), RecipeCache.getCachedRecipe(this));
			sendUpdate();
		}
	}
	
	@Override
	public void tickServer(Level lvl, BlockPos pos, BlockState st) {
		performAutoInputItems(lvl, pos);
		performAutoOutputItems(lvl, pos);
		
		if (selectedRecipeIndex < 0 || recipes.isEmpty() || selectedRecipeIndex >= recipes.size()) {
			progress = 0;
			return;
		}
		
		StonecutterRecipe recipe = (StonecutterRecipe) recipes.get(selectedRecipeIndex).value();
		progress(() -> {
			ItemStack result = recipe.assemble(recipeContainer.get(), lvl.registryAccess());
			inventory.extractItem(0, 1, false);
			inventory.insertItemForced(1, result, false);
		});
	}
}
