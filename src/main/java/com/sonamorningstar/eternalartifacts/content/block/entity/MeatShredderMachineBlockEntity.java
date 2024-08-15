package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.caches.RecipeCache;
import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.container.MeatShredderMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MeatShredderMachineBlockEntity extends SidedTransferMachineBlockEntity<MeatShredderMenu>  {
    RecipeCache<MeatShredderRecipe, SimpleContainer> recipeCache = new RecipeCache<>();

    public MeatShredderMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MEAT_SHREDDER.get(), pos, blockState, MeatShredderMenu::new);
        setInventory(new ModItemStorage(1) {
            @Override
            protected void onContentsChanged(int slot) {
                progress = 0;
                MeatShredderMachineBlockEntity.this.sendUpdate();
                recipeCache.findRecipe(ModRecipes.MEAT_SHREDDING_TYPE.get(), new SimpleContainer(inventory.getStackInSlot(0)), level);
            }
        });
        setEnergy(createDefaultEnergy());
        setTank(createBasicTank(16000, fs -> fs.is(ModTags.Fluids.MEAT), true, false));
    }


    @Override
    public void onLoad() {
        super.onLoad();
        recipeCache.findRecipe(ModRecipes.MEAT_SHREDDING_TYPE.get(), new SimpleContainer(inventory.getStackInSlot(0)), level);
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
