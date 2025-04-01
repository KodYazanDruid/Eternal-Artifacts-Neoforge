package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.client.gui.screen.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.GenericSidedMachineScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        ModMachines.MACHINES.getGenericMachines().forEach(machine -> event.register(machine.getMenu(), GenericSidedMachineScreen::new));

        event.register(ModMachines.OIL_REFINERY.getMenu(), GenericSidedMachineScreen::new);
        event.register(ModMachines.ELECTRIC_FURNACE.getMenu(), ElectricFurnaceScreen::new);
        event.register(ModMachines.INDUCTION_FURNACE.getMenu(), InductionFurnaceScreen::new);

        event.register(ModMenuTypes.CHARMS.get(), CharmsScreen::new);
        event.register(ModMenuTypes.FISH.get(), FishScreen::new);
        event.register(ModMenuTypes.BLUEPRINT.get(), BlueprintScreen::new);
        event.register(ModMenuTypes.PORTABLE_BATTERY.get(), PortableBatteryScreen::new);
        event.register(ModMenuTypes.MACHINE_WORKBENCH.get(), MachineWorkbenchScreen::new);
    }
}
