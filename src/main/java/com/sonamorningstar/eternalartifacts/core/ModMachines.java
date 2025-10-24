package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.container.*;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.OilRefineryBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.BaseMachineBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineSixWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.GenericMachine;
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
    public static final GenericMachineHolder<FluidMixer> FLUID_MIXER = registerGeneric("fluid_mixer", FluidMixer::new);
    public static final GenericMachineHolder<Disenchanter> DISENCHANTER = registerGeneric("disenchanter", Disenchanter::new);
    public static final GenericMachineHolder<MobHarvester> MOB_HARVESTER = registerGeneric("mob_harvester", MobHarvester::new);
    public static final GenericMachineHolder<MarineFisher> MARINE_FISHER = registerGeneric("marine_fisher", MarineFisher::new);
    public static final GenericMachineHolder<Smithinator> SMITHINATOR = registerGeneric("smithinator", Smithinator::new);
    public static final GenericMachineHolder<DimensionalAnchor> DIMENSIONAL_ANCHOR = registerGeneric("dimensional_anchor", DimensionalAnchor::new);
    public static final GenericMachineHolder<Repairer> REPAIRER = registerGeneric("repairer", Repairer::new);
    public static final GenericMachineHolder<Recycler> RECYCLER = registerGeneric("recycler", Recycler::new);
    public static final GenericMachineHolder<Packer> PACKER = registerGeneric("packer", Packer::new);
    public static final GenericMachineHolder<Unpacker> UNPACKER = registerGeneric("unpacker", Unpacker::new);
    
    public static final MachineDeferredHolder<ElectricFurnaceMenu, ElectricFurnace, BaseMachineBlock<ElectricFurnace>, MachineBlockItem>
        ELECTRIC_FURNACE = MACHINES.register("electric_furnace", ElectricFurnaceMenu::new, ElectricFurnace::new);
    public static final MachineDeferredHolder<AutoCutterMenu, AutoCutter, BaseMachineBlock<AutoCutter>, MachineBlockItem>
        AUTOCUTTER = MACHINES.register("autocutter", AutoCutterMenu::new, 3, AutoCutter::new);
    public static final MachineDeferredHolder<GenericMachineMenu, OilRefinery, OilRefineryBlock<OilRefinery>, BewlrMachineItem>
        OIL_REFINERY = MACHINES.register("oil_refinery", GenericMachineMenu::new, OilRefinery::new, OilRefineryBlock::new, BewlrMachineItem::new, true, true);
    public static final MachineDeferredHolder<AdvancedCrafterMenu, AdvancedCrafter, BaseMachineBlock<AdvancedCrafter>, MachineBlockItem>
        ADVANCED_CRAFTER = MACHINES.register("advanced_crafter", AdvancedCrafterMenu::new, AdvancedCrafter::new);
    public static final MachineDeferredHolder<InductionFurnaceMenu, InductionFurnace, BaseMachineBlock<InductionFurnace>, MachineBlockItem>
        INDUCTION_FURNACE = MACHINES.register("induction_furnace", InductionFurnaceMenu::new, InductionFurnace::new);
    public static final MachineDeferredHolder<AlchemicalBrewerMenu, AlchemicalBrewer, BaseMachineBlock<AlchemicalBrewer>, MachineBlockItem>
        ALCHEMICAL_BREWER = MACHINES.register("alchemical_brewer", AlchemicalBrewerMenu::new, AlchemicalBrewer::new);
    public static final MachineDeferredHolder<BottlerMenu, Bottler, BaseMachineBlock<Bottler>, MachineBlockItem>
        BOTTLER = MACHINES.register("bottler", BottlerMenu::new, Bottler::new);
    public static final MachineDeferredHolder<HarvesterMenu, Harvester, BaseMachineBlock<Harvester>, MachineBlockItem>
        HARVESTER = MACHINES.register("harvester", HarvesterMenu::new, Harvester::new);
    
    
    public static final MachineDeferredHolder<GenericMachineMenu, BlockPlacer, MachineSixWayBlock<BlockPlacer>, MachineBlockItem>
        BLOCK_PLACER = MACHINES.register("block_placer", GenericMachineMenu::new, BlockPlacer::new, MachineSixWayBlock::new, MachineBlockItem::new, false, false);
    public static final MachineDeferredHolder<GenericMachineMenu, BlockBreaker, MachineSixWayBlock<BlockBreaker>, MachineBlockItem>
        BLOCK_BREAKER = MACHINES.register("block_breaker", GenericMachineMenu::new, BlockBreaker::new, MachineSixWayBlock::new, MachineBlockItem::new, false, false);

    private static <T extends GenericMachine> GenericMachineHolder<T> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<T> supp) {
        return MACHINES.registerGeneric(name, supp,false);
    }
    private static <T extends GenericMachine> GenericMachineHolder<T> registerGeneric(String name, BlockEntityType.BlockEntitySupplier<T> supp, boolean hasCustomRender) {
        return MACHINES.registerGeneric(name, supp,false, hasCustomRender);
    }
}