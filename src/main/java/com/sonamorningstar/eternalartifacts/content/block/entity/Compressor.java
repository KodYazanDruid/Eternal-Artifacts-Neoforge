package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.CompressorRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class Compressor extends GenericMachineBlockEntity {
    public Compressor(BlockPos pos, BlockState blockState) {
        super(ModMachines.COMPRESSOR, pos, blockState);
        outputSlots.add(1);
        setInventory(() -> createRecipeFinderInventory(2, outputSlots));
        setEnergy(this::createDefaultEnergy);
        setRecipeTypeAndContainer(ModRecipes.COMPRESSING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0)));
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        if (recipe instanceof CompressorRecipe compressor) {
            condition
                .queueImport(compressor.getOutput())
                .commitQueuedImports();
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        CompressorRecipe recipe = (CompressorRecipe) RecipeCache.getCachedRecipe(this);
        if (recipe == null) {
            progress = 0;
            return;
        }
        progress(() -> {
            inventory.insertItemForced(1, recipe.getOutput().copy(), false);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        });
    }
}
