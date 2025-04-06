package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.SolidifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class Solidifier extends GenericMachineBlockEntity {
    public Solidifier(BlockPos pos, BlockState blockState) {
        super(ModMachines.SOLIDIFIER, pos, blockState);
        setInventory(() -> createBasicInventory(1, false));
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createRecipeFinderTank(16000,true, true));
        setRecipeTypeAndContainer(ModRecipes.SOLIDIFYING.getType(), () -> new SimpleFluidContainer(tank.getFluid(0)));
        outputSlots.add(0);
        screenInfo.setArrowXOffset(-40);
    }
    
    @Override
    protected void setProcessCondition(ProcessCondition condition, @Nullable Recipe<?> recipe) {
        if (recipe instanceof SolidifierRecipe solidifier) {
            condition.initInputTank(tank)
                .initOutputTank(tank)
                .queueImport(solidifier.getOutput())
                .commitQueuedImports();
                
        }
        super.setProcessCondition(condition, recipe);
    }
    
    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputFluids(lvl, pos);

        SolidifierRecipe recipe = (SolidifierRecipe) RecipeCache.getCachedRecipe(this);

        if (recipe == null) {
            progress = 0;
            return;
        }

        progress(() -> {
            ItemHelper.insertItemStackedForced(inventory, recipe.getResultItem(lvl.registryAccess()).copy(), false, outputSlots);
            tank.drainForced(recipe.getInputFluid().getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
        });
    }
}
