package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.IHasEnergy;
import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import com.sonamorningstar.eternalartifacts.container.BatteryBoxMenu;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BatteryBoxBlockEntity extends SidedTransferBlockEntity<BatteryBoxMenu> implements IHasEnergy {
    public BatteryBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BATTERY_BOX.get(), pos, blockState, BatteryBoxMenu::new);
    }

    @Getter
    public ModEnergyStorage energy = new ModEnergyStorage(25000, 1000) {
        @Override
        public void onEnergyChanged() {
            BatteryBoxBlockEntity.this.sendUpdate();
        }
    };

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energy.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energy.deserializeNBT(tag.get("Energy"));
    }

    @Override
    public void tick(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputEnergy(lvl, pos, energy);
        performAutoOutputEnergy(lvl, pos, energy);
    }
}
