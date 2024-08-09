package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.container.MeatPackerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
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

@Getter
public class MeatPackerMachineBlockEntity extends SidedTransferMachineBlockEntity<MeatPackerMenu> implements IHasInventory, IHasFluidTank, IHasEnergy {
    public MeatPackerMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MEAT_PACKER.get(), pos, blockState, MeatPackerMenu::new);
    }

    public ModItemStorage inventory = createBasicInventory(1, false);
    public ModEnergyStorage energy = createDefaultEnergy();
    public ModFluidStorage tank = createBasicTank(10000, fs -> fs.is(ModTags.Fluids.MEAT), true, true);

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
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
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
