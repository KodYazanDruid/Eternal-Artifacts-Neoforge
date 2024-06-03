package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.capabilities.IHasEnergy;
import com.sonamorningstar.eternalartifacts.capabilities.ModEnergyStorage;
import lombok.Getter;
import net.minecraft.world.item.Item;

public class BatteryItem extends Item implements IHasEnergy {
    public BatteryItem(Properties pProperties) {
        super(pProperties);
    }

    @Getter
    public ModEnergyStorage energy = new ModEnergyStorage(10000, 500) {
        @Override
        public void onEnergyChanged() {

        }
    };




}
