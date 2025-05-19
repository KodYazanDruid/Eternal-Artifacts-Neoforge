package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.recipe.MeltingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class MeltingCrucible extends GenericMachine {
    public MeltingCrucible(BlockPos pos, BlockState blockState) {
        super(ModMachines.MELTING_CRUCIBLE, pos, blockState);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createBasicTank(16000, true, false));
        setInventory(() -> createRecipeFinderInventory(1, outputSlots));
        setRecipeTypeAndContainer(ModRecipes.MELTING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0)));
        screenInfo.attachTankToLeft(0);
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        if (recipe instanceof MeltingRecipe melting) {
            condition
                .initOutputTank(tank)
                .queueImport(melting.getOutput())
                .commitQueuedImports();
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoOutputFluids(lvl, pos);
        MeltingRecipe recipe = (MeltingRecipe) RecipeCache.getCachedRecipe(this);
        if (recipe == null) {
            progress = 0;
            return;
        }

        progress(() -> {
            tank.fillForced(recipe.getOutput(), IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        });
    }
}