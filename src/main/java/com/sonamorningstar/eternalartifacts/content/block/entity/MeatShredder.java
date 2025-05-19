package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
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

public class MeatShredder extends GenericMachine {
    public MeatShredder(BlockPos pos, BlockState blockState) {
        super(ModMachines.MEAT_SHREDDER, pos, blockState);
        setInventory(() -> createRecipeFinderInventory(1, outputSlots));
        setEnergy(this::createDefaultEnergy);
        setTank(() -> createBasicTank(16000, fs -> fs.is(ModTags.Fluids.MEAT), true, false));
        setRecipeTypeAndContainer(ModRecipes.MEAT_SHREDDING.getType(), () -> new SimpleContainer(inventory.getStackInSlot(0)));
        screenInfo.attachTankToLeft(0);
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        performAutoInputItems(lvl, pos);
        performAutoOutputFluids(lvl, pos);
        MeatShredderRecipe recipe = (MeatShredderRecipe) RecipeCache.getCachedRecipe(this);
        if(recipe != null) {
            FluidStack fs = recipe.getOutput();
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
