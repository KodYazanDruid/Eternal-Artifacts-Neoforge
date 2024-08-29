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
        event.register(ModMachines.MEAT_PACKER.getMenu(), GenericSidedMachineScreen::new);
        event.register(ModMachines.FLUID_INFUSER.getMenu(), GenericSidedMachineScreen::new);
        event.register(ModMachines.INDUSTRIAL_MACERATOR.getMenu(), GenericSidedMachineScreen::new);
        event.register(ModMachines.MATERIAL_SQUEEZER.getMenu(), GenericSidedMachineScreen::new);

        event.register(ModMachines.INDUCTION_FURNACE.getMenu(), InductionFurnaceScreen::new);
        event.register(ModMachines.MELTING_CRUCIBLE.getMenu(), MeltingCrucibleScreen::new);
    }
}
