package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.api.machine.config.BatteryBoxExportConfig;
import com.sonamorningstar.eternalartifacts.api.machine.config.ReverseToggleConfig;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModularEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.container.BatteryBoxMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachine;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.List;

public class BatteryBox extends SidedTransferMachine<BatteryBoxMenu> {
    public BatteryBox(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BATTERY_BOX.get(), pos, blockState, BatteryBoxMenu::new);
        setInventory(() -> new ModItemStorage(4) {
            @Override
            protected void onContentsChanged(int slot) {
                ((ModularEnergyStorage) energy).reloadEnergyHandlers(new RecipeWrapper(this));
                BatteryBox.this.sendUpdate();
                BatteryBox.this.invalidateCapabilities();
            }
        });
        setEnergy(() -> new ModularEnergyStorage(new RecipeWrapper(inventory)) {
            @Override
            public void onEnergyChanged() {
                BatteryBox.this.sendUpdate();
            }
        });
        setEnergyTransferRate(Integer.MAX_VALUE);
        registerCapabilityConfigs(Capabilities.EnergyStorage.BLOCK);
    }
    
    @Override
    public void registerCapabilityConfigs(BlockCapability<?, ?> cap) {
        super.registerCapabilityConfigs(cap);
        if (cap == Capabilities.EnergyStorage.BLOCK && energy != null) getConfiguration().add(new ReverseToggleConfig("energy_transfer"));
    }
    
    @Override
    public void registerConfigs() {
        super.registerConfigs();
        getConfiguration().add(new BatteryBoxExportConfig());
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
        super.tickServer(lvl, pos, st);
        performAutoInputItems(lvl, pos);
        performAutoOutputItems(lvl, pos, List.of(0, 1, 2, 3), stack -> {
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energyStorage != null) {
                BatteryBoxExportConfig config = getConfiguration().get(BatteryBoxExportConfig.class);
                float stackPercent = energyStorage.getEnergyStored() * 100 / (float) energyStorage.getMaxEnergyStored();
                if (config != null) {
                    return switch (config.getExportMode()) {
                        case FULL -> stackPercent >= 100.0F;
                        case EMPTY -> stackPercent <= 0.0F;
                        case PERCENTAGE_BELOW -> stackPercent <= config.getPercentage();
                        case PERCENTAGE_ABOVE -> stackPercent >= config.getPercentage();
                        case PERCENTAGE_EXACT -> Math.abs(stackPercent - config.getPercentage()) < 0.01F;
                    };
                }
            }
            return true;
        });
        performAutoInputEnergy(lvl, pos);
        performAutoOutputEnergy(lvl, pos);
    }
}
