package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class Resonator extends ModBlockEntity {
    public Resonator(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RESONATOR.get(), pPos, pBlockState);
    }

    public final ModEnergyStorage energy = new ModEnergyStorage(2560, 256) {
        @Override
        public void onEnergyChanged() {
            sendUpdate();
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    public void tick(Level lvl, BlockPos pos, BlockState st, int rate) {
        energy.receiveEnergyForced(rate, false);
        distributePower();
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        energy.deserializeNBT(pTag.get("Energy"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", energy.serializeNBT());
    }

    private void distributePower() {
        if(energy.getEnergyStored() <= 0 || level == null) return;
        Direction direction = getBlockState().getValue(BlockStateProperties.FACING);
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(direction));
        if(be != null) {
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), direction.getOpposite());
            if(target != null) {
                int received = target.receiveEnergy(energy.getEnergyStored(), false);
                energy.extractEnergyForced(received, false);
            }
        }
    }
}
