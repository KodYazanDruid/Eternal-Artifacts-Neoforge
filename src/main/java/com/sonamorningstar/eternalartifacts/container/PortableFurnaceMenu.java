package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemItemStorage;
import com.sonamorningstar.eternalartifacts.container.slot.CapRefreshedItemSlot;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public class PortableFurnaceMenu extends TabMenu {
	public final ItemStack furnace;
	public final ContainerData data;
	public final List<Ingredient> smeltables;
	public PortableFurnaceMenu(int id, Inventory inv, ItemStack furnace, ContainerData data) {
		super(ModMenuTypes.PORTABLE_FURNACE.get(), id, inv);
		this.furnace = furnace;
		this.data = data;
		smeltables = inv.player.level().getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)
				.stream()
				.map(RecipeHolder::value)
				.map(SmeltingRecipe::getIngredients)
				.map(list -> list.get(0))
				.toList();
		addPlayerInventoryAndHotbar(inv, 8, 66);
		addDataSlots(data);
		IItemHandler itemHandler = this.furnace.getCapability(Capabilities.ItemHandler.ITEM);
		if (itemHandler instanceof ModItemItemStorage) {
			addSlot(new CapRefreshedItemSlot(() -> furnace.getCapability(Capabilities.ItemHandler.ITEM), 0, 56, 17) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return smeltables.stream().anyMatch(ingredient -> ingredient.test(stack));
				}
			});
			addSlot(new CapRefreshedItemSlot(() -> furnace.getCapability(Capabilities.ItemHandler.ITEM), 1, 56, 53) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.getBurnTime(RecipeType.SMELTING) > 0 || stack.is(Items.BUCKET);
				}
				@Override
				public int getMaxStackSize(ItemStack stack) {
					return stack.is(Items.BUCKET) ? 1 : super.getMaxStackSize(stack);
				}
			});
			addSlot(new CapRefreshedItemSlot(() -> furnace.getCapability(Capabilities.ItemHandler.ITEM), 2, 116, 35) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return false;
				}
			});
		}
	}
	
	public boolean hasFuel() {
		return data.get(0) > 0;
	}
	
	public float getFuelProgress() {
		return (float)data.get(0) / (float)data.get(1);
	}
	
	public float getRecipeProgress() {
		return (float)data.get(2) / (float)data.get(3);
	}
	
	public static PortableFurnaceMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf extraData) {
		return new PortableFurnaceMenu(id, inv, extraData.readItem(), new SimpleContainerData(4));
	}
}
