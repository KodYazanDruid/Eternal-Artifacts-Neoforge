package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.client.gui.screen.FluidInfuserScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.InductionFurnaceScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.MeltingCrucibleScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.base.GenericSidedMachineScreen;
import com.sonamorningstar.eternalartifacts.container.FluidInfuserMenu;
import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidInfuserBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.InductionFurnaceBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.IndustrialMaceratorBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.MeltingCrucibleBlockEntity;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredRegister;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMachines {
    public static final MachineDeferredRegister MACHINES = new MachineDeferredRegister(MODID);

    public static final MachineDeferredHolder<FluidInfuserMenu, FluidInfuserScreen, FluidInfuserBlockEntity, MachineFourWayBlock<FluidInfuserBlockEntity>, BlockItem>
            FLUID_INFUSER = MACHINES.register("fluid_infuser", FluidInfuserMenu::new, FluidInfuserScreen::new, FluidInfuserBlockEntity::new);
    public static final MachineDeferredHolder<InductionFurnaceMenu, InductionFurnaceScreen, InductionFurnaceBlockEntity, MachineFourWayBlock<InductionFurnaceBlockEntity>, BlockItem>
            INDUCTION_FURNACE = MACHINES.register("induction_furnace", InductionFurnaceMenu::new, InductionFurnaceScreen::new, InductionFurnaceBlockEntity::new);
     public static final MachineDeferredHolder<GenericMachineMenu, GenericSidedMachineScreen, IndustrialMaceratorBlockEntity, MachineFourWayBlock<IndustrialMaceratorBlockEntity>, BlockItem>
            INDUSTRIAL_MACERATOR = registerGeneric("industrial_macerator", IndustrialMaceratorBlockEntity::new);
    public static final MachineDeferredHolder<GenericMachineMenu, MeltingCrucibleScreen, MeltingCrucibleBlockEntity, MachineFourWayBlock<MeltingCrucibleBlockEntity>, BlockItem>
            MELTING_CRUCIBLE = registerGenericWithScreen("melting_crucible", MeltingCrucibleBlockEntity::new, MeltingCrucibleScreen::new);

    private static <T extends GenericMachineBlockEntity> MachineDeferredHolder<GenericMachineMenu, GenericSidedMachineScreen, T, MachineFourWayBlock<T>, BlockItem> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<T> supp) {
        return MACHINES.register(name, GenericMachineMenu::new, GenericSidedMachineScreen::new, supp);
    }

    private static <S extends AbstractSidedMachineScreen<GenericMachineMenu>, T extends GenericMachineBlockEntity> MachineDeferredHolder<GenericMachineMenu, S, T, MachineFourWayBlock<T>, BlockItem> registerGenericWithScreen(String name, BlockEntityType.BlockEntitySupplier<T> supp, MenuScreens.ScreenConstructor<GenericMachineMenu, S> screenSup) {
        return MACHINES.register(name, GenericMachineMenu::new, screenSup, supp);
    }

}
