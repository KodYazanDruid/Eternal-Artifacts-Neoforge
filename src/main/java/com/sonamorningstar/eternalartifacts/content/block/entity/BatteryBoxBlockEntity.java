package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModularEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.BatteryBoxMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class BatteryBoxBlockEntity extends SidedTransferMachineBlockEntity<BatteryBoxMenu> {
    public BatteryBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BATTERY_BOX.get(), pos, blockState, BatteryBoxMenu::new);
        setInventory(new ModItemStorage(4) {
            @Override
            protected void onContentsChanged(int slot) {
                ((ModularEnergyStorage) energy).reloadEnergyHandlers(new RecipeWrapper(this));
                BatteryBoxBlockEntity.this.sendUpdate();
                BatteryBoxBlockEntity.this.invalidateCapabilities();
            }
        });
        setEnergy(new ModularEnergyStorage(new RecipeWrapper(inventory)) {
            @Override
            public void onEnergyChanged() {
                BatteryBoxBlockEntity.this.sendUpdate();
            }
        });
        setEnergyTransferRate(Integer.MAX_VALUE);
    }

    @Override
    protected boolean shouldSerializeEnergy() {
        return false;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ((ModularEnergyStorage) energy).reloadEnergyHandlers(new RecipeWrapper(inventory));
        sendUpdate();
    }

    @Override
    public void tickServer(Level lvl, BlockPos pos, BlockState st) {
        performAutoInputEnergy(lvl, pos, energy);
        performAutoOutputEnergy(lvl, pos, energy);
    }
}
