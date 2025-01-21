package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.MeltingRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MeltingCrucibleBlockEntity extends GenericMachineBlockEntity {
    public MeltingCrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.MELTING_CRUCIBLE, pos, blockState);
        initializeDefaultEnergyAndTank();
        setTank(createBasicTank(16000, true, false));
        setInventory(createRecipeFinderInventory(1, outputSlots));
        setRecipeTypeAndContainer(ModRecipes.MELTING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0)));
        screenInfo.attachTankToLeft(0);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoOutputFluids(lvl, pos);
        MeltingRecipe recipe = recipeCache.getRecipe(MeltingRecipe.class);
        if (recipe == null) {
            progress = 0;
            return;
        }

        ProcessCondition condition = new ProcessCondition()
                .initInventory(inventory)
                .initOutputTank(tank)
                .tryExtractItemForced(recipe.getInput().getItems()[0].getCount())
                .tryInsertForced(recipe.getOutput());

        progress(condition::getResult, () -> {
            tank.fillForced(recipe.getOutput(), IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        }, energy);
    }
}