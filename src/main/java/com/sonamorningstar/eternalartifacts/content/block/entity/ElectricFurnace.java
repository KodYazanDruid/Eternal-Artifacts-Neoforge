package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.container.ElectricFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricFurnace extends SidedTransferMachineBlockEntity<ElectricFurnaceMenu> {
    public ElectricFurnace(BlockPos pos, BlockState blockState) {
        super(ModMachines.ELECTRIC_FURNACE.getBlockEntity(), pos, blockState, (a, b, c, d) -> new ElectricFurnaceMenu(ModMachines.ELECTRIC_FURNACE.getMenu(), a, b, c, d));
        setEnergy(this::createDefaultEnergy);
        outputSlots.add(1);
        setInventory(() -> createRecipeFinderInventory(2, outputSlots));
        setRecipeContainer(() -> new SimpleContainer(inventory.getStackInSlot(0)));
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putShort("RecipeType", recipeTypeId);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        recipeTypeId = tag.getShort("RecipeType");
    }
    
    @Override
    public void saveContents(CompoundTag additionalTag) {
        super.saveContents(additionalTag);
        additionalTag.putShort("RecipeType", recipeTypeId);
    }
    
    @Override
    public void loadContents(CompoundTag additionalTag) {
        super.loadContents(additionalTag);
        recipeTypeId = additionalTag.getShort("RecipeType");
    }
    
    public short recipeTypeId = 0;
    
    public RecipeType<? extends Recipe<? extends Container>> getSelectedRecipeType() {
		return switch (recipeTypeId) {
			case 1 -> RecipeType.BLASTING;
			case 2 -> RecipeType.SMOKING;
			case 3 -> RecipeType.CAMPFIRE_COOKING;
			default -> RecipeType.SMELTING;
		};
    }
    
    public void setRecipeTypeId(short id) {
        recipeTypeId = id;
        findRecipe();
        sendUpdate();
    }

    @Override
    protected void findRecipe() {
        recipeType = getSelectedRecipeType();
        super.findRecipe();
    }
    
    @Override
    public void setProcessCondition(ProcessCondition condition, Recipe<?> recipe) {
        if (recipe != null && level != null) {
            ItemStack result = ((Recipe<Container>) recipe).assemble(recipeContainer.get(), level.registryAccess()).copy();
            result.onCraftedBySystem(level);
            condition.queueImport(result).commitQueuedItemStackImports();
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos);
        
        Recipe<Container> recipe = (Recipe<Container>) RecipeCache.getCachedRecipe(this);
        if (recipe == null) {
            progress = 0;
            return;
        }

        ItemStack result = recipe.assemble(recipeContainer.get(), lvl.registryAccess()).copy();
        int cost = recipe.getIngredients().get(0).getItems()[0].getCount();

        progress(() -> {
            result.onCraftedBySystem(lvl);
            inventory.insertItemForced(1, result, false);
            inventory.extractItem(0, cost, false);
        });
    }
}
