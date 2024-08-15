package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.container.FluidInfuserMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FluidInfuserBlockEntity extends SidedTransferMachineBlockEntity<FluidInfuserMenu> {
    public FluidInfuserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModMachines.FLUID_INFUSER.getBlockEntity(), pos, blockState, FluidInfuserMenu::new);
        outputSlots.add(1);
        setInventory(createBasicInventory(2, outputSlots.toArray(Integer[]::new)));
        initializeDefaultEnergyAndTank();
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInput(lvl, pos, inventory);
        performAutoOutput(lvl, pos, inventory, outputSlots.toArray(Integer[]::new));
        performAutoInputFluids(lvl, pos, tank);
    }
}
