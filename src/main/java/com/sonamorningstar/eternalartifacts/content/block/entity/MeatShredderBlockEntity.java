package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MeatShredderBlockEntity extends GenericMachineBlockEntity {
    RecipeCache<MeatShredderRecipe, SimpleContainer> recipeCache = new RecipeCache<>();

    public MeatShredderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.MEAT_SHREDDER, pos, blockState);
        setInventory(createRecipeFinderInventory(1, outputSlots));
        setEnergy(createDefaultEnergy());
        setTank(createBasicTank(16000, fs -> fs.is(ModTags.Fluids.MEAT), true, false));
        screenInfo.attachTankToLeft(0);
    }

    @Override
    protected void findRecipe() {
        recipeCache.findRecipe(ModRecipes.MEAT_SHREDDING.getType(), new SimpleContainer(inventory.getStackInSlot(0)), level);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInput(lvl, pos, inventory);
        performAutoOutputFluids(lvl, pos, tank);
        if(recipeCache.getRecipe() != null) {
            FluidStack fs = recipeCache.getRecipe().getOutput();
            progress(()-> {
                int inserted = tank.fillForced(fs, IFluidHandler.FluidAction.SIMULATE);
                return inserted < fs.getAmount();
            }, () -> {
                inventory.extractItem(0, 1, false);
                tank.fillForced(fs, IFluidHandler.FluidAction.EXECUTE);
            }, energy);
        }
    }

}
