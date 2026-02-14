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
        event.register(ModMachines.BLOCK_BREAKER.getMenu(), BlockInteractorScreen::new);
        event.register(ModMachines.BLOCK_PLACER.getMenu(), BlockInteractorScreen::new);
        event.register(ModMachines.AUTOCUTTER.getMenu(), AutoCutterScreen::new);
        event.register(ModMachines.ADVANCED_CRAFTER.getMenu(), AdvancedCrafterScreen::new);
        event.register(ModMachines.ALCHEMICAL_BREWER.getMenu(), AlchemicalBrewerScreen::new);
        event.register(ModMachines.BOTTLER.getMenu(), BottlerScreen::new);
        event.register(ModMachines.HARVESTER.getMenu(), HarvesterScreen::new);
        event.register(ModMachines.ANVILINATOR.getMenu(), AnvilinatorScreen::new);

        event.register(ModMenuTypes.CHARMS.get(), CharmsScreen::new);
        event.register(ModMenuTypes.FISH.get(), FishScreen::new);
        event.register(ModMenuTypes.BLUEPRINT.get(), BlueprintScreen::new);
        event.register(ModMenuTypes.PORTABLE_BATTERY.get(), PortableBatteryScreen::new);
        event.register(ModMenuTypes.PORTABLE_FURNACE.get(), PortableFurnaceScreen::new);
        event.register(ModMenuTypes.MACHINE_WORKBENCH.get(), MachineWorkbenchScreen::new);
        event.register(ModMenuTypes.PIPE_FILTER.get(), PipeFilterScreen::new);
        event.register(ModMenuTypes.PIPE_FILTER_ITEM.get(), PipeFilterItemScreen::new);
        event.register(ModMenuTypes.SOLAR_PANEL.get(), SolarPanelScreen::new);
        event.register(ModMenuTypes.INTERFACE_REMOTE.get(), InterfaceRemoteScreen::new);
    }
}
