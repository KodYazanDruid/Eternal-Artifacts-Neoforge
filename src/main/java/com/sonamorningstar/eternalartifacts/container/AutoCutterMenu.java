package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.AutoCutter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoCutterMenu extends AbstractMachineMenu {
	public List<Runnable> inputListeners = new ArrayList<>();
	public AutoCutterMenu(@Nullable MenuType<?> menuType, int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(menuType, id, inv, entity, data);
		if (beInventory != null) {
			addSlot(new SlotItemHandler(beInventory, 0, 26, 40){
				@Override
				public void setChanged() {
					super.setChanged();
					runInputListeners();
				}
			});
			addSlot(new SlotItemHandler(beInventory, 1, 145, 40));
		}
	}
	
	private void runInputListeners() {
		inputListeners.forEach(Runnable::run);
	}
	
	public List<RecipeHolder<Recipe<Container>>> getRecipes() {
		return ((AutoCutter) getBlockEntity()).getRecipes();
	}
	
	public int getSelectedRecipeIndex() {
		return data.get(2);
	}
	
	@Override
	public boolean clickMenuButton(Player pPlayer, int pId) {
		if (blockEntity instanceof AutoCutter autoCutter) {
			autoCutter.setSelectedRecipeIndex(pId);
			return true;
		}
		return false;
	}
}
