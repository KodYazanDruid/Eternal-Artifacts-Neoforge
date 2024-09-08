package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.MaceratingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class IndustrialMaceratorBlockEntity extends GenericMachineBlockEntity {
    public IndustrialMaceratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.INDUSTRIAL_MACERATOR, pos, blockState);
        setEnergy(createDefaultEnergy());
        outputSlots.add(1);
        setInventory(createBasicInventory(2, outputSlots, i -> {
            if (!outputSlots.contains(i)) findRecipe();
        }));
    }

    private final RecipeCache<MaceratingRecipe, SimpleContainer> recipeCache = new RecipeCache<>();

    @Override
    protected void findRecipe() {
        this.recipeCache.findRecipe(ModRecipes.MACERATING.getType(), new SimpleContainer(inventory.getStackInSlot(0)), level);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        MaceratingRecipe recipe = recipeCache.getRecipe();
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
            ItemHelper.insertItemStackedForced(inventory, recipe.getOutput().copy(), false, outputSlots.toArray(Integer[]::new));
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        }, energy);
    }
}
