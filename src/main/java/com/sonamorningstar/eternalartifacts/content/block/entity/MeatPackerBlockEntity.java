package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.container.MeatPackerMenu;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class MeatPackerBlockEntity extends SidedTransferBlockEntity<MeatPackerMenu> implements IHasInventory, IHasFluidTank, IHasEnergy {
    public MeatPackerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MEAT_PACKER.get(), pos, blockState, MeatPackerMenu::new);
    }

    @Getter
    public ModItemStorage inventory = new ModItemStorage(1) {
        @Override
        protected void onContentsChanged(int slot) {
            MeatPackerBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };

    @Getter
    public ModEnergyStorage energy = new ModEnergyStorage(50000, 2500) {
        @Override
        public void onEnergyChanged() {
            MeatPackerBlockEntity.this.sendUpdate();
        }
    };

    @Getter
    public ModFluidStorage tank = new ModFluidStorage(10000) {
        @Override
        protected void onContentsChanged() {
            MeatPackerBlockEntity.this.sendUpdate();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.is(ModTags.Fluids.MEAT);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }

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

    //buggy
    @Override
    public void tick(Level lvl, BlockPos pos, BlockState st) {
        performAutoOutput(lvl, pos, inventory, 0);
        performAutoInputFluids(lvl, pos, tank);
        progress(()-> {
            ItemStack remainder = inventory.insertItemForced(0, ModItems.RAW_MEAT_INGOT.toStack(), true);
            FluidStack drained = tank.drainForced(250, IFluidHandler.FluidAction.SIMULATE);
            return !remainder.isEmpty() || drained.getAmount() < 250;
        }, ()-> {
            tank.drainForced(250, IFluidHandler.FluidAction.EXECUTE);
            inventory.insertItemForced(0, ModItems.RAW_MEAT_INGOT.toStack(), false);
        }, energy);

    }

}
