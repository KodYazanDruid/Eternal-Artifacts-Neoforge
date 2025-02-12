package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidInfuserRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.ItemFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidInfuserBlockEntity extends GenericMachineBlockEntity {
    public FluidInfuserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.FLUID_INFUSER, pos, blockState);
        outputSlots.add(1);
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createBasicTank(16000, this::findRecipe));
        setInventory(() -> createRecipeFinderInventory(2, outputSlots));
        setRecipeTypeAndContainer(ModRecipes.FLUID_INFUSING.getType(), () -> new ItemFluidContainer(inventory, tank));
        screenInfo.setArrowXOffset(-20);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos);
        performAutoInputFluids(lvl, pos);
        FluidInfuserRecipe recipe = recipeCache.getRecipe(FluidInfuserRecipe.class);
        if (recipe == null) {
            progress = 0;
            return;
        }
        ProcessCondition condition = new ProcessCondition()
                .initInputTank(tank)
                .initInventory(inventory)
                .initOutputSlots(outputSlots)
                .tryExtractFluidForced(recipe.getInputFluid().getFluidStacks()[0].getAmount())
                .tryExtractItemForced(recipe.getInput().getItems()[0].getCount())
                .tryInsertForced(recipe.getOutput());
        progress(condition::getResult, ()-> {
            ItemHelper.insertItemStackedForced(inventory, recipe.getResultItem(lvl.registryAccess()).copy(), false, outputSlots);
            tank.drainForced(recipe.getInputFluid().getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        }, energy);
    }
}
