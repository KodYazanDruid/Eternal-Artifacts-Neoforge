package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
import com.sonamorningstar.eternalartifacts.content.recipe.SqueezingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class MaterialSqueezer extends GenericMachine {
    public MaterialSqueezer(BlockPos pos, BlockState blockState) {
        super(ModMachines.MATERIAL_SQUEEZER, pos, blockState);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createBasicTank(16000, true, false));
        outputSlots.add(1);
        setInventory(() -> createRecipeFinderInventory(2, outputSlots));
        setRecipeTypeAndContainer(ModRecipes.SQUEEZING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0)));
        screenInfo.attachTankToLeft(0);
        screenInfo.setArrowXOffset(-20);
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        if (recipe instanceof SqueezingRecipe squeezing) {
            condition
                .initOutputTank(tank)
                .queueImport(squeezing.getOutput())
                .queueImport(squeezing.getOutputFluid())
                .commitQueuedImports();
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoOutputFluids(lvl, pos);

        SqueezingRecipe recipe = (SqueezingRecipe) RecipeCache.getCachedRecipe(this);
        if (recipe == null) {
            progress = 0;
            return;
        }

        progress(() -> {
            inventory.insertItemForced(1, recipe.getResultItem(lvl.registryAccess()).copy(), false);
            tank.fillForced(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, 1, false);
        });
    }
}
