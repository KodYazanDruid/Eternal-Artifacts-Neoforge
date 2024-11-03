package com.sonamorningstar.eternalartifacts.compat.pneumaticcraft;

import com.sonamorningstar.eternalartifacts.capabilities.handler.IHeatHandler;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerAdapter;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PneumaticCraftCompat {

    public static void registerHeat(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(PNCCapabilities.HEAT_EXCHANGER_BLOCK, ModMachines.INDUCTION_FURNACE.getBlockEntity(),
                (be, dir) -> wrapHeatCapability(be.heat, dir));
    }

    private static IHeatExchangerLogic wrapHeatCapability(IHeatHandler eaHeat, Direction dir) {
        return new IHeatExchangerAdapter.Simple<>(dir, eaHeat, 0) {
            @Override
            public double getTemperature() {
                return Mth.lerp((double) eaHeat.getHeat() / eaHeat.getMaxHeat(), 300D, Math.max(eaHeat.getHeat() / 10D, 300D));
            }

            @Override
            public double getThermalResistance() {
                return 500;
            }

            @Override
            public double getThermalCapacity() {
                return eaHeat.getMaxHeat() / 10D;
            }

            @Override
            public void addHeat(double v) {
                if (v == 0) return;
                if (v > 0) eaHeat.heat((int) (v * 10), false);
                else eaHeat.cool((int) (v * -10), false);
            }
        };
    }
}
