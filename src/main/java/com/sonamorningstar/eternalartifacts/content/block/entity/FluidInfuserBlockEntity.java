package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.api.machine.ProcessCondition;
import com.sonamorningstar.eternalartifacts.container.FluidInfuserMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.FluidInfuserRecipe;
import com.sonamorningstar.eternalartifacts.content.recipe.container.ItemFluidContainer;
import com.sonamorningstar.eternalartifacts.content.recipe.container.SimpleFluidContainer;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Collections;

public class FluidInfuserBlockEntity extends SidedTransferMachineBlockEntity<FluidInfuserMenu> {
    public FluidInfuserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.FLUID_INFUSER.getBlockEntity(), pos, blockState, (a, b, c, d) -> new FluidInfuserMenu(ModMachines.FLUID_INFUSER.getMenu(), a, b, c, d));
        outputSlots.add(1);
        setEnergy(createDefaultEnergy());
        setTank(createBasicTank(16000, this::findRecipe));
        setInventory(createBasicInventory(2, outputSlots, slot -> {
            if (!outputSlots.contains(slot)) findRecipe();
        }));
    }

    private final RecipeCache<FluidInfuserRecipe, ItemFluidContainer> cache = new RecipeCache<>();

    private void findRecipe() {
        this.cache.findRecipe(ModRecipes.FLUID_INFUSING.getType(), new ItemFluidContainer(inventory, tank), level);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        findRecipe();
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInput(lvl, pos, inventory);
        performAutoOutput(lvl, pos, inventory, outputSlots.toArray(Integer[]::new));
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
            tank.drainForced(recipe.getInputFluid().getFluidStacks()[0], IFluidHandler.FluidAction.EXECUTE);
            inventory.extractItem(0, recipe.getInput().getItems()[0].getCount(), false);
        }, energy);
    }
}
