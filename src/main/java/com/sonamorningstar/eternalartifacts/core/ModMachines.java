package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.container.ElectricFurnaceMenu;
import com.sonamorningstar.eternalartifacts.container.InductionFurnaceMenu;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.OilRefineryBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import com.sonamorningstar.eternalartifacts.content.item.block.base.BewlrMachineItem;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import com.sonamorningstar.eternalartifacts.registrar.GenericMachineHolder;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredHolder;
import com.sonamorningstar.eternalartifacts.registrar.MachineDeferredRegister;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMachines {
    public static final MachineDeferredRegister MACHINES = new MachineDeferredRegister(MODID);

    public static final GenericMachineHolder<MobLiquifier> MOB_LIQUIFIER = registerGeneric("mob_liquifier", MobLiquifier::new);
    public static final GenericMachineHolder<MeatShredder> MEAT_SHREDDER = registerGeneric("meat_shredder", MeatShredder::new);
    public static final GenericMachineHolder<MeatPacker> MEAT_PACKER = registerGeneric("meat_packer", MeatPacker::new);

    public static final GenericMachineHolder<FluidInfuser> FLUID_INFUSER = registerGeneric("fluid_infuser", FluidInfuser::new);
    public static final GenericMachineHolder<IndustrialMacerator> INDUSTRIAL_MACERATOR = registerGeneric("industrial_macerator", IndustrialMacerator::new);
    public static final GenericMachineHolder<MeltingCrucible> MELTING_CRUCIBLE = registerGeneric("melting_crucible", MeltingCrucible::new);
    public static final GenericMachineHolder<MaterialSqueezer> MATERIAL_SQUEEZER = registerGeneric("material_squeezer", MaterialSqueezer::new);
    public static final GenericMachineHolder<AlloySmelter> ALLOY_SMELTER = registerGeneric("alloy_smelter", AlloySmelter::new);
    public static final GenericMachineHolder<Solidifier> SOLIDIFIER = registerGeneric("solidifier", Solidifier::new);
    public static final GenericMachineHolder<Compressor> COMPRESSOR = registerGeneric("compressor", Compressor::new);
    public static final GenericMachineHolder<AdvancedCrafter> ADVANCED_CRAFTER = registerGeneric("advanced_crafter", AdvancedCrafter::new);
    public static final GenericMachineHolder<FluidMixer> FLUID_MIXER = registerGeneric("fluid_mixer", FluidMixer::new);
    
    public static final MachineDeferredHolder<ElectricFurnaceMenu, ElectricFurnace, MachineFourWayBlock<ElectricFurnace>, MachineBlockItem>
        ELECTRIC_FURNACE = MACHINES.register("electric_furnace", ElectricFurnaceMenu::new, ElectricFurnace::new);
    public static final MachineDeferredHolder<GenericMachineMenu, OilRefinery, OilRefineryBlock<OilRefinery>, BewlrMachineItem>
            OIL_REFINERY = MACHINES.register("oil_refinery", GenericMachineMenu::new, OilRefinery::new, OilRefineryBlock::new, BewlrMachineItem::new, true, true);

    public static final MachineDeferredHolder<InductionFurnaceMenu, InductionFurnace, MachineFourWayBlock<InductionFurnace>, MachineBlockItem>
            INDUCTION_FURNACE = MACHINES.register("induction_furnace", InductionFurnaceMenu::new, InductionFurnace::new);

    private static <T extends GenericMachineBlockEntity> GenericMachineHolder<T> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<T> supp) {
        return MACHINES.registerGeneric(name, supp,false);
    }
    private static <T extends GenericMachineBlockEntity> GenericMachineHolder<T> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<T> supp, boolean hasCustomRender) {
        return MACHINES.registerGeneric(name, supp,false, hasCustomRender);
    }
}