package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.container.BatteryBoxMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public class BatteryBoxBlockEntity extends SidedTransferMachineBlockEntity<BatteryBoxMenu> {
    public BatteryBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BATTERY_BOX.get(), pos, blockState, BatteryBoxMenu::new);
        setInventory(new ModItemStorage(4) {
            @Override
            protected void onContentsChanged(int slot) {
                ((ModularEnergyStorage) energy).reloadEnergyHandlers(simpleContainer(this));
                BatteryBoxBlockEntity.this.sendUpdate();
                BatteryBoxBlockEntity.this.invalidateCapabilities();
            }
        });
        setEnergy(new ModularEnergyStorage(simpleContainer(inventory)) {
            @Override
            public void onEnergyChanged() {
                BatteryBoxBlockEntity.this.sendUpdate();
            }
        });
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ((ModularEnergyStorage) energy).reloadEnergyHandlers(simpleContainer(inventory));
        sendUpdate();
        invalidateCapabilities();
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputEnergy(lvl, pos, energy);
        performAutoOutputEnergy(lvl, pos, energy);
    }

    private SimpleContainer simpleContainer(IItemHandler handler) {
        SimpleContainer container = new SimpleContainer(handler.getSlots());
        for(int i = 0; i < handler.getSlots(); i++) container.setItem(i, handler.getStackInSlot(i));
        return container;
    }
}
