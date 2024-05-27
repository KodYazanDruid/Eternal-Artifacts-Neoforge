package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.container.MeatShredderMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.MeatShredderRecipe;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModRecipes;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class MeatShredderBlockEntity extends SidedTransferBlockEntity<MeatShredderMenu> implements IHasInventory, IHasFluidTank, IHasEnergy {
    public MeatShredderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MEAT_SHREDDER.get(), pos, blockState, MeatShredderMenu::new);
    }

    private MeatShredderRecipe currentRecipe = null;

    @Getter
    public ModItemStorage inventory = new ModItemStorage(1) {
        @Override
        protected void onContentsChanged(int slot) {
            progress = 0;
            MeatShredderBlockEntity.this.sendUpdate();
        }
    };

    @Getter
    public ModEnergyStorage energy = new ModEnergyStorage(50000, 2500) {
        @Override
        public void onEnergyChanged() {
            MeatShredderBlockEntity.this.sendUpdate();
        }
    };

    @Getter
    public ModFluidStorage tank = new ModFluidStorage(10000) {
        @Override
        protected void onContentsChanged() {
            MeatShredderBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.is(ModTags.Fluids.MEAT);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return 0; }
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.put("Energy", energy.serializeNBT());
        tank.writeToNBT(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        energy.deserializeNBT(tag.get("Energy"));
        tank.readFromNBT(tag);
    }

    @Override
    public void tick(Level lvl, BlockPos pos, BlockState st) {
        if(progress <= 0) findRecipe();
        performAutoInput(lvl, pos, inventory);
        performAutoOutputFluids(lvl, pos, tank);

        if(currentRecipe != null && hasEnergy(consume, energy)) progress(currentRecipe.getOutput());

    }

    private void progress(FluidStack fs) {
        int inserted = tank.fillForced(fs, IFluidHandler.FluidAction.SIMULATE);
        if(inserted < currentRecipe.getOutput().getAmount()) {
            progress = 0;
            return;
        }
        energy.extractEnergyForced(consume, false);
        progress++;
        if (progress >= maxProgress) {
            ItemStack stack = inventory.getStackInSlot(0);
            stack.shrink(1);
            tank.fillForced(fs, IFluidHandler.FluidAction.EXECUTE);
            progress = 0;
        }
    }

    private void findRecipe() {
        SimpleContainer container = new SimpleContainer(inventory.getStackInSlot(0));
        if(currentRecipe != null && currentRecipe.matches(container, level)) return;
        currentRecipe = null;
        List<MeatShredderRecipe> recipeList = level.getRecipeManager().getAllRecipesFor(ModRecipes.MEAT_SHREDDING_TYPE.get()).stream().map(RecipeHolder::value).toList();
        for(MeatShredderRecipe recipe : recipeList) {
            if(recipe.matches(container, level)) {
                currentRecipe = recipe;
                return;
            }
        }
    }

}
