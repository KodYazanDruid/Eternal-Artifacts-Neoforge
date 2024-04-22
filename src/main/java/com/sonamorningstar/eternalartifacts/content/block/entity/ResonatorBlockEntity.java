package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capablities.ModEnergyStorage;
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

public class ResonatorBlockEntity extends ModBlockEntity {
    public ResonatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RESONATOR.get(), pPos, pBlockState);
    }

    public final ModEnergyStorage energy = new ModEnergyStorage(2560, 256) {
        @Override
        public void onEnergyChanged() {
            sendUpdate();
        }
    };

    public void tick(Level lvl, BlockPos pos, BlockState st, int rate) {
        energy.receiveEnergy(rate, false);
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
        if(energy.getEnergyStored() <= 0) return;
        Direction direction = getBlockState().getValue(BlockStateProperties.FACING);
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(direction));
        if(be != null) {
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), direction.getOpposite());
            if(target != null && target.canReceive()) {
                int received = target.receiveEnergy(Math.min(energy.getEnergyStored(), energy.getMaxEnergyStored()), false);
                energy.extractEnergy(received, false);
                setChanged();
            }
        }

    }
}
