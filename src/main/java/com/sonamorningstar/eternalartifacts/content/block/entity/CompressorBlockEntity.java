package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.CompressorRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CompressorBlockEntity extends GenericMachineBlockEntity {
    public CompressorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.COMPRESSOR, pos, blockState);
        outputSlots.add(1);
        setInventory(createRecipeFinderInventory(2, outputSlots));
        setEnergy(createDefaultEnergy());
    }

    private final RecipeCache<CompressorRecipe, SimpleContainer> recipeCache = new RecipeCache<>();

    @Override
    protected void findRecipe() {
        recipeCache.findRecipe(ModRecipes.COMPRESSING.getType(), new SimpleContainer(inventory.getStackInSlot(0)), level);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        CompressorRecipe recipe = recipeCache.getRecipe();
        if (recipe == null) {
            progress = 0;
            return;
        }
        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputSlots(outputSlots)
                .tryExtractItemForced(recipe.getInput().getItems()[0].getCount())
                .tryInsertForced(recipe.getOutput());
        progress(condition::getResult, () -> {
            inventory.insertItemForced(1, recipe.getOutput().copy(), false);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        }, energy);
    }
}
