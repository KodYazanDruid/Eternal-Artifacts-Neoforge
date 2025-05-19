package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.recipe.MaceratingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class IndustrialMacerator extends GenericMachine {
    public IndustrialMacerator(BlockPos pos, BlockState blockState) {
        super(ModMachines.INDUSTRIAL_MACERATOR, pos, blockState);
        setEnergy(this::createDefaultEnergy);
        outputSlots.add(1);
        setInventory(() -> createRecipeFinderInventory(2, outputSlots));
        setRecipeTypeAndContainer(ModRecipes.MACERATING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0)));
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        if (recipe instanceof MaceratingRecipe macerating) {
            condition
                .queueImport(macerating.getOutput())
                .commitQueuedImports();
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        MaceratingRecipe recipe = (MaceratingRecipe) getCachedRecipe();
        if (recipe == null) {
            progress = 0;
            return;
        };
        progress(() -> {
            ItemHelper.insertItemStackedForced(inventory, recipe.getOutput().copy(), false, outputSlots);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        });
    }
}
