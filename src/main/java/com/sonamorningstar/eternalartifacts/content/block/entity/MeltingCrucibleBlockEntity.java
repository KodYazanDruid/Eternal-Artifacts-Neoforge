package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
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
        super(ModMachines.MELTING_CRUCIBLE.getBlockEntity(), pos, blockState, ModMachines.MELTING_CRUCIBLE.getMenu());
        initializeDefaultEnergyAndTank();
        setEnergy(createDefaultEnergy());
        setTank(createBasicTank(16000, true, false));
        setInventory(createBasicInventory(1, true, i -> findRecipe()));
    }

    private final RecipeCache<MeltingRecipe, SimpleContainer> recipeCache = new RecipeCache<>();

    private void findRecipe() {
        this.recipeCache.findRecipe(ModRecipes.MELTING.getType(), new SimpleContainer(inventory.getStackInSlot(0)), level);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        findRecipe();
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoOutputFluids(lvl, pos, tank);
        MeltingRecipe recipe = recipeCache.getRecipe();
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