package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.client.gui.screen.FluidInfuserScreen;
import com.sonamorningstar.eternalartifacts.container.FluidInfuserMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.FluidInfuserBlockEntity;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredRegister;
import net.minecraft.world.item.BlockItem;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMachines {
    public static final MachineDeferredRegister MACHINES = new MachineDeferredRegister(MODID);

    public static final MachineDeferredHolder<FluidInfuserMenu, FluidInfuserScreen, FluidInfuserBlockEntity, MachineFourWayBlock<FluidInfuserBlockEntity>, BlockItem>
            FLUID_INFUSER = MACHINES.register("fluid_infuser", FluidInfuserMenu::new, FluidInfuserScreen::new, FluidInfuserBlockEntity::new);

}
