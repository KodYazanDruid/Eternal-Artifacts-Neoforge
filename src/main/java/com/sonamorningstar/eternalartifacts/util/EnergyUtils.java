package com.sonamorningstar.eternalartifacts.util;

import com.sonamorningstar.eternalartifacts.capabilities.energy.ModEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyUtils {

    public static int transferEnergy(IEnergyStorage source, IEnergyStorage target, int maxTransfer) {
        return transferEnergy(source, target, maxTransfer, false);
    }
    public static int transferEnergy(IEnergyStorage source, IEnergyStorage target, int maxTransfer, boolean simulate) {
        int extracted = source.extractEnergy(maxTransfer, true);
        if (extracted > 0) {
            int received = target.receiveEnergy(extracted, true);
            if (received > 0) {
                target.receiveEnergy(received, simulate);
                source.extractEnergy(received, simulate);
                return received;
            }
        }
        return 0;
    }
    public static int transferEnergyForced(IEnergyStorage source, ModEnergyStorage target, int maxTransfer) {
        return transferEnergyForced(source, target, maxTransfer, false);
    }
    public static int transferEnergyForced(IEnergyStorage source, ModEnergyStorage target, int maxTransfer, boolean simulate) {
        int extracted = source.extractEnergy(maxTransfer, true);
        if (extracted > 0) {
            int received = target.receiveEnergyForced(extracted, true);
            if (received > 0) {
                target.receiveEnergyForced(received, simulate);
                source.extractEnergy(received, simulate);
                return received;
            }
        }
        return 0;
    }
}
