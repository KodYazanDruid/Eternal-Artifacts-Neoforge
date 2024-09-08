package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.AlloyingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AlloySmelterBlockEntity extends GenericMachineBlockEntity {
    public AlloySmelterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.ALLOY_SMELTER, pos, blockState);
        setEnergy(createDefaultEnergy());
        outputSlots.add(3);
        setInventory(createRecipeFinderInventory(4, outputSlots));
        screenInfo.setArrowXOffset(16);
    }

    private final RecipeCache<AlloyingRecipe, SimpleContainer> recipeCache = new RecipeCache<>();

    @Override
    protected void findRecipe() {
        recipeCache.findRecipe(ModRecipes.ALLOYING.getType(), new SimpleContainer(inventory.getStackInSlot(0), inventory.getStackInSlot(1), inventory.getStackInSlot(2)), level);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots);

        AlloyingRecipe recipe = recipeCache.getRecipe();
        if (recipe != null) {
            condition.tryInsertForced(recipe.getResultItem(lvl.registryAccess()));
        }

        progress(condition::getResult, () -> {
            inventory.insertItemForced(3, recipe.getResultItem(lvl.registryAccess()).copy(), false);
            inventory.extractItem(0, 1, false);
            inventory.extractItem(1, 1, false);
            inventory.extractItem(2, 1, false);
        }, energy);
    }
}
