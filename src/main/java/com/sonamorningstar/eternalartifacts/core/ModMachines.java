package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMachines {
    public static final MachineDeferredRegister MACHINES = new MachineDeferredRegister(MODID);

    public static final MachineDeferredHolder<GenericMachineMenu, MeatPackerBlockEntity, MachineFourWayBlock<MeatPackerBlockEntity>, BlockItem>
            MEAT_PACKER = registerGeneric("meat_packer", MeatPackerBlockEntity::new);

    public static final MachineDeferredHolder<GenericMachineMenu, FluidInfuserBlockEntity, MachineFourWayBlock<FluidInfuserBlockEntity>, BlockItem>
            FLUID_INFUSER = registerGeneric("fluid_infuser", FluidInfuserBlockEntity::new);
    public static final MachineDeferredHolder<InductionFurnaceMenu, InductionFurnaceBlockEntity, MachineFourWayBlock<InductionFurnaceBlockEntity>, BlockItem>
            INDUCTION_FURNACE = MACHINES.register("induction_furnace", InductionFurnaceMenu::new, InductionFurnaceBlockEntity::new);
    public static final MachineDeferredHolder<GenericMachineMenu, IndustrialMaceratorBlockEntity, MachineFourWayBlock<IndustrialMaceratorBlockEntity>, BlockItem>
            INDUSTRIAL_MACERATOR = registerGeneric("industrial_macerator", IndustrialMaceratorBlockEntity::new);
    public static final MachineDeferredHolder<GenericMachineMenu, MeltingCrucibleBlockEntity, MachineFourWayBlock<MeltingCrucibleBlockEntity>, BlockItem>
            MELTING_CRUCIBLE = registerGeneric("melting_crucible", MeltingCrucibleBlockEntity::new);
    public static final MachineDeferredHolder<GenericMachineMenu, MaterialSqueezerBlockEntity, MachineFourWayBlock<MaterialSqueezerBlockEntity>, BlockItem>
            MATERIAL_SQUEEZER = registerGeneric("material_squeezer", MaterialSqueezerBlockEntity::new);

    private static <T extends GenericMachineBlockEntity> MachineDeferredHolder<GenericMachineMenu, T, MachineFourWayBlock<T>, BlockItem> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<T> supp) {
        return MACHINES.register(name, GenericMachineMenu::new, supp);
    }
}