package com.sonamorningstar.eternalartifacts.content.block.entity;

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

public class MeatShredderMachineBlockEntity extends SidedTransferMachineBlockEntity<MeatShredderMenu> implements IHasInventory, IHasFluidTank, IHasEnergy {
    public MeatShredderMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MEAT_SHREDDER.get(), pos, blockState, MeatShredderMenu::new);
    }

    @Getter
    public ModItemStorage inventory = new ModItemStorage(1) {
        @Override
        protected void onContentsChanged(int slot) {
            progress = 0;
            findRecipe(ModRecipes.MEAT_SHREDDING_TYPE.get(), new SimpleContainer(inventory.getStackInSlot(0)));
            MeatShredderMachineBlockEntity.this.sendUpdate();
        }
    };

    @Getter
    public ModEnergyStorage energy = new ModEnergyStorage(50000, 2500) {
        @Override
        public void onEnergyChanged() {
            MeatShredderMachineBlockEntity.this.sendUpdate();
        }
    };

    @Getter
    public ModFluidStorage tank = new ModFluidStorage(10000) {
        @Override
        protected void onContentsChanged() {
            MeatShredderMachineBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.is(ModTags.Fluids.MEAT);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return 0; }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        findRecipe(ModRecipes.MEAT_SHREDDING_TYPE.get(), new SimpleContainer(inventory.getStackInSlot(0)));
    }

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
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInput(lvl, pos, inventory);
        performAutoOutputFluids(lvl, pos, tank);
        if(currentRecipe instanceof MeatShredderRecipe msr) {
            FluidStack fs = msr.getOutput();
            progress(()-> {
                int inserted = tank.fillForced(fs, IFluidHandler.FluidAction.SIMULATE);
                return inserted < fs.getAmount();
            }, () -> {
                inventory.getStackInSlot(0).shrink(1);
                tank.fillForced(fs, IFluidHandler.FluidAction.EXECUTE);
            }, energy);
        }
    }
}
