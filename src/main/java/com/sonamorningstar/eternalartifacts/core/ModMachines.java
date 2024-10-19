package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.OilRefineryBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import com.sonamorningstar.eternalartifacts.content.item.block.base.BEWLRBlockItem;
import com.sonamorningstar.eternalartifacts.registrar.GenericMachineHolder;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMachines {
    public static final MachineDeferredRegister MACHINES = new MachineDeferredRegister(MODID);

    public static final GenericMachineHolder<MobLiquifierBlockEntity> MOB_LIQUIFIER = registerGeneric("mob_liquifier", MobLiquifierBlockEntity::new);
    public static final GenericMachineHolder<MeatShredderBlockEntity> MEAT_SHREDDER = registerGeneric("meat_shredder", MeatShredderBlockEntity::new);
    public static final GenericMachineHolder<MeatPackerBlockEntity> MEAT_PACKER = registerGeneric("meat_packer", MeatPackerBlockEntity::new);

    public static final GenericMachineHolder<FluidInfuserBlockEntity> FLUID_INFUSER = registerGeneric("fluid_infuser", FluidInfuserBlockEntity::new);
    public static final GenericMachineHolder<IndustrialMaceratorBlockEntity> INDUSTRIAL_MACERATOR = registerGeneric("industrial_macerator", IndustrialMaceratorBlockEntity::new);
    public static final GenericMachineHolder<MeltingCrucibleBlockEntity> MELTING_CRUCIBLE = registerGeneric("melting_crucible", MeltingCrucibleBlockEntity::new);
    public static final GenericMachineHolder<MaterialSqueezerBlockEntity> MATERIAL_SQUEEZER = registerGeneric("material_squeezer", MaterialSqueezerBlockEntity::new);
    public static final GenericMachineHolder<AlloySmelterBlockEntity> ALLOY_SMELTER = registerGeneric("alloy_smelter", AlloySmelterBlockEntity::new);
    public static final GenericMachineHolder<SolidifierBlockEntity> SOLIDIFIER = registerGeneric("solidifier", SolidifierBlockEntity::new);

    public static final MachineDeferredHolder<GenericMachineMenu, OilRefineryBlockEntity, OilRefineryBlock<OilRefineryBlockEntity>, BEWLRBlockItem>
            OIL_REFINERY = MACHINES.register("oil_refinery", GenericMachineMenu::new, OilRefineryBlockEntity::new, OilRefineryBlock::new, BEWLRBlockItem::new, true, true);

    public static final MachineDeferredHolder<InductionFurnaceMenu, InductionFurnaceBlockEntity, MachineFourWayBlock<InductionFurnaceBlockEntity>, BlockItem>
            INDUCTION_FURNACE = MACHINES.register("induction_furnace", InductionFurnaceMenu::new, InductionFurnaceBlockEntity::new);


    private static <T extends GenericMachineBlockEntity> GenericMachineHolder<T> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<T> supp) {
        return MACHINES.registerGeneric(name, supp,false);
    }
    private static <T extends GenericMachineBlockEntity> GenericMachineHolder<T> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<T> supp, boolean hasCustomRender) {
        return MACHINES.registerGeneric(name, supp,false, hasCustomRender);
    }
}