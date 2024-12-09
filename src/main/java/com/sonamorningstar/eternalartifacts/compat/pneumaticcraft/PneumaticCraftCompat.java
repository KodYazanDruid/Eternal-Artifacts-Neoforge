package com.sonamorningstar.eternalartifacts.compat.pneumaticcraft;

import com.sonamorningstar.eternalartifacts.capabilities.handler.IHeatHandler;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.event.hooks.ModHooks;
import me.desht.pneumaticcraft.PneumaticCraftRepressurized;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerAdapter;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import me.desht.pneumaticcraft.common.registry.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.function.Supplier;

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

    public static void run(IEventBus modEventBus) {
        //ModHooks.ItemTagAppender.appendTag(ModTags.Items.CHARMS_CHARM, ModItems.MEMORY_STICK::get);
        addTag(ModTags.Items.CHARMS_CHARM, ModItems.MEMORY_STICK::get);
    }

    private static void addTag(TagKey<Item> tagKey, Supplier<Item> item) {
        ModHooks.ItemTagAppender.appendTag(tagKey, item);
    }
}
