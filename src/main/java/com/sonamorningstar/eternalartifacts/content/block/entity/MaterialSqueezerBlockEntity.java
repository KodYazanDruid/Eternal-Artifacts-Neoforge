package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.SqueezingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MaterialSqueezerBlockEntity extends GenericMachineBlockEntity {
    public MaterialSqueezerBlockEntity(BlockPos pos, BlockState blockState) {
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
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoOutputFluids(lvl, pos);

        SqueezingRecipe recipe = recipeCache.getRecipe(SqueezingRecipe.class);
        if (recipe == null) {
            progress = 0;
            return;
        }

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputTank(tank)
                .initOutputSlots(outputSlots)
                .tryExtractItemForced(recipe.getInput().getItems()[0].getCount())
                .tryInsertForced(recipe.getOutput())
                .tryInsertForced(recipe.getOutputFluid());

        progress(condition::getResult, () -> {
            inventory.insertItemForced(1, recipe.getOutput(), false);
            tank.fillForced(recipe.getOutputFluid(), IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, 1, false);
        }, energy);
    }
}
