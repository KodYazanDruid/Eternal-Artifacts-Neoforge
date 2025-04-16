package com.sonamorningstar.eternalartifacts.capabilities.energy;

import com.sonamorningstar.eternalartifacts.content.entity.ChargedSheepEntity;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.UpdateEntityEnergyToClient;

public class ChargedSheepEnergy extends ModEnergyStorage{
    private final ChargedSheepEntity sheep;
    public ChargedSheepEnergy(ChargedSheepEntity sheep, int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
        this.sheep = sheep;
    }

    @Override
    public void onEnergyChanged() {
        if (!sheep.level().isClientSide)
            Channel.sendToSelfAndTracking(new UpdateEntityEnergyToClient(sheep.getId(), getEnergyStored()), sheep);
        sheep.getEntityData().set(ChargedSheepEntity.ENERGY, energy);
    }
}
