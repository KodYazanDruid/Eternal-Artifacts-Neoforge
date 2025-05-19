package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.recipe.AlloyingRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.ingredient.SizedIngredient;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AlloySmelter extends GenericMachine {
    public AlloySmelter(BlockPos pos, BlockState blockState) {
        super(ModMachines.ALLOY_SMELTER, pos, blockState);
        setEnergy(this::createDefaultEnergy);
        outputSlots.add(3);
        setInventory(() -> createRecipeFinderInventory(4, outputSlots));
        setRecipeTypeAndContainer(ModRecipes.ALLOYING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0), inventory.getStackInSlot(1), inventory.getStackInSlot(2)));
        screenInfo.setArrowXOffset(16);
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition processCondition, Recipe<?> recipe) {
        if (recipe instanceof AlloyingRecipe alloying) {
            processCondition
                .queueImport(alloying.getResultItem(level.registryAccess()))
                .commitQueuedImports();
        }
        super.setProcessCondition(processCondition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);

        AlloyingRecipe recipe = ((AlloyingRecipe) RecipeCache.getCachedRecipe(this));
        if (recipe == null) {
            progress = 0;
            return;
        }

        progress(() -> {
            inventory.insertItemForced(3, recipe.getResultItem(lvl.registryAccess()).copy(), false);
            for (int i = 0; i < 3; i++) {
                ItemStack inputItem = inventory.getStackInSlot(i);
                if (!inputItem.is(ModItems.SLOT_LOCK) && !inputItem.isEmpty()) {
                    for (SizedIngredient input : recipe.getInputs()) {
                        for (ItemStack item : input.getItems()) {
                            if (item.is(inputItem.getItem())) {
                                inventory.extractItem(i, item.getCount(), false);
                            }
                        }
                    }
                }
            }
        });
    }
}
