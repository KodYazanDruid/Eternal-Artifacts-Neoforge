package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.container.ElectricFurnaceMenu;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricFurnace extends MultiFurnace<ElectricFurnaceMenu> {
    public ElectricFurnace(BlockPos pos, BlockState blockState) {
        super(ModMachines.ELECTRIC_FURNACE.getBlockEntity(), pos, blockState, (a, b, c, d) -> new ElectricFurnaceMenu(ModMachines.ELECTRIC_FURNACE.getMenu(), a, b, c, d));
        setEnergy(this::createDefaultEnergy);
        outputSlots.add(1);
        setInventory(() -> createRecipeFinderInventory(2, outputSlots));
        setRecipeContainer(() -> new SimpleContainer(inventory.getStackInSlot(0)));
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
