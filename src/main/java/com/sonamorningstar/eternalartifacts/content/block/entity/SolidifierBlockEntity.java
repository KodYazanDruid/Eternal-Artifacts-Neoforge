package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.SolidifierRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class SolidifierBlockEntity extends GenericMachineBlockEntity {
    public SolidifierBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.SOLIDIFIER, pos, blockState);
        setInventory(createBasicInventory(1, false));
        setEnergy(createDefaultEnergy());
        setTank(createRecipeFinderTank(16000,true, true));
        outputSlots.add(0);
        screenInfo.setArrowXOffset(-40);
    }

    private final RecipeCache<SolidifierRecipe, SimpleFluidContainer> recipeCache = new RecipeCache<>();

    @Override
    protected void findRecipe() {
        recipeCache.findRecipe(ModRecipes.SOLIDIFYING.getType(), new SimpleFluidContainer(tank.getFluid(0)), level);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputFluids(lvl, pos, tank);

        SolidifierRecipe recipe = recipeCache.getRecipe();

        if (recipe == null) {
            progress = 0;
            return;
        }

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initInputTank(tank)
                .initOutputSlots(outputSlots)
                .tryExtractFluidForced(recipe.getInputFluid().getFluidStacks()[0].getAmount())
                .tryInsertForced(recipe.getOutput());

        progress(condition::getResult, () -> {
            ItemHelper.insertItemStackedForced(inventory, recipe.getResultItem(lvl.registryAccess()).copy(), false, outputSlots.toArray(Integer[]::new));
            tank.drainForced(recipe.getInputFluid().getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }, energy);
    }
}