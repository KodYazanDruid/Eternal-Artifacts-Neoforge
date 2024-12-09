package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
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
        setEnergy(createDefaultEnergy());
        setTank(createBasicTank(16000, this::findRecipe));
        setInventory(createBasicInventory(2, outputSlots, slot -> {
            if (!outputSlots.contains(slot)) findRecipe();
        }));
        screenInfo.setArrowXOffset(-20);
    }

    private final RecipeCache<FluidInfuserRecipe, ItemFluidContainer> cache = new RecipeCache<>();

    @Override
    protected void findRecipe() {
        this.cache.findRecipe(ModRecipes.FLUID_INFUSING.getType(), new ItemFluidContainer(inventory, tank), level);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputItems(lvl, pos, inventory);
        performAutoOutputItems(lvl, pos, inventory, outputSlots.toArray(Integer[]::new));
        performAutoInputFluids(lvl, pos, tank);
        FluidInfuserRecipe recipe = cache.getRecipe();
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
            ItemHelper.insertItemStackedForced(inventory, recipe.getResultItem(lvl.registryAccess()).copy(), false, outputSlots.toArray(Integer[]::new));
            tank.drainForced(recipe.getInputFluid().getFluidStacks()[0].getAmount(), IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        }, energy);
    }
}
