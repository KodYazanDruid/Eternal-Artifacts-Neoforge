package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.MachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ShockAbsorberBlockEntity extends MachineBlockEntity<AbstractMachineMenu> {
    public ShockAbsorberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOCK_ABSORBER.get(), pos, state, null);
        setEnergy(() -> createBasicEnergy(100000, 100000, false, true));
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        super.tickServer(lvl, pos, st);
        for (Direction value : Direction.values()) {
            if (hasAnyEnergy(energy)) outputEnergyToDir(lvl, pos, value, energy);
        }
    }

}
