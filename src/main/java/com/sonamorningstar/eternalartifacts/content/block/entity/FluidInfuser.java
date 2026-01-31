package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidInfuserRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.ItemFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidInfuser extends GenericMachine {
    public FluidInfuser(BlockPos pos, BlockState blockState) {
        super(ModMachines.FLUID_INFUSER, pos, blockState);
        outputSlots.add(1);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createRecipeFinderTank(16000));
        setInventory(() -> createRecipeFinderInventory(2, outputSlots));
        setRecipeTypeAndContainer(ModRecipes.FLUID_INFUSING.getType(), () -> new ItemFluidContainer(inventory, tank));
        screenInfo.setArrowXOffset(-20);
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        if (recipe instanceof FluidInfuserRecipe fluidInfuser) {
            condition
                .initInputTank(tank)
                .tryExtractFluidForced(fluidInfuser.getInputFluid().getFluidStacks()[0].getAmount())
                .queueImport(fluidInfuser.getOutput())
                .commitQueuedImports();
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos);
        performAutoInputFluids(lvl, pos);
        FluidInfuserRecipe recipe = (FluidInfuserRecipe) RecipeCache.getCachedRecipe(this);
        if (recipe == null) {
            progress = 0;
            return;
        }
        
        progress(()-> {
            ItemHelper.insertItemStackedForced(inventory, recipe.getResultItem(lvl.registryAccess()).copy(), false, outputSlots);
            tank.drainForced(recipe.getInputFluid().getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        });
    }
}
