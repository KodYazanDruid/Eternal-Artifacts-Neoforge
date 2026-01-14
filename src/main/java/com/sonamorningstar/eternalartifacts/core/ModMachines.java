package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.container.*;
import com.sonamorningstar.eternalartifacts.container.base.GenericMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.OilRefineryBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineSixWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import com.sonamorningstar.eternalartifacts.content.item.block.base.BewlrMachineItem;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import com.sonamorningstar.eternalartifacts.registrar.MachineHolder;
import com.sonamorningstar.eternalartifacts.registrar.MachineRegistration;
import com.sonamorningstar.eternalartifacts.registrar.MachineRegistry;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModMachines {
    public static final MachineRegistry MACHINES = new MachineRegistry(MODID);

    // ==================== Generic Machines ====================
    public static final MachineHolder<GenericMachineMenu, MobLiquifier, MachineFourWayBlock<MobLiquifier>, MachineBlockItem>
            MOB_LIQUIFIER = MACHINES.registerGeneric("mob_liquifier", MobLiquifier::new);
    
    public static final MachineHolder<GenericMachineMenu, MeatShredder, MachineFourWayBlock<MeatShredder>, MachineBlockItem>
            MEAT_SHREDDER = MACHINES.registerGeneric("meat_shredder", MeatShredder::new);
    
    public static final MachineHolder<GenericMachineMenu, MeatPacker, MachineFourWayBlock<MeatPacker>, MachineBlockItem>
            MEAT_PACKER = MACHINES.registerGeneric("meat_packer", MeatPacker::new);
    
    public static final MachineHolder<GenericMachineMenu, FluidInfuser, MachineFourWayBlock<FluidInfuser>, MachineBlockItem>
            FLUID_INFUSER = MACHINES.registerGeneric("fluid_infuser", FluidInfuser::new);
    
    public static final MachineHolder<GenericMachineMenu, IndustrialMacerator, MachineFourWayBlock<IndustrialMacerator>, MachineBlockItem>
            INDUSTRIAL_MACERATOR = MACHINES.registerGeneric("industrial_macerator", IndustrialMacerator::new);
    
    public static final MachineHolder<GenericMachineMenu, MeltingCrucible, MachineFourWayBlock<MeltingCrucible>, MachineBlockItem>
            MELTING_CRUCIBLE = MACHINES.registerGeneric("melting_crucible", MeltingCrucible::new);
    
    public static final MachineHolder<GenericMachineMenu, MaterialSqueezer, MachineFourWayBlock<MaterialSqueezer>, MachineBlockItem>
            MATERIAL_SQUEEZER = MACHINES.registerGeneric("material_squeezer", MaterialSqueezer::new);
    
    public static final MachineHolder<GenericMachineMenu, AlloySmelter, MachineFourWayBlock<AlloySmelter>, MachineBlockItem>
            ALLOY_SMELTER = MACHINES.registerGeneric("alloy_smelter", AlloySmelter::new);
    
    public static final MachineHolder<GenericMachineMenu, Solidifier, MachineFourWayBlock<Solidifier>, MachineBlockItem>
            SOLIDIFIER = MACHINES.registerGeneric("solidifier", Solidifier::new);
    
    public static final MachineHolder<GenericMachineMenu, Compressor, MachineFourWayBlock<Compressor>, MachineBlockItem>
            COMPRESSOR = MACHINES.registerGeneric("compressor", Compressor::new);
    
    public static final MachineHolder<GenericMachineMenu, FluidMixer, MachineFourWayBlock<FluidMixer>, MachineBlockItem>
            FLUID_MIXER = MACHINES.registerGeneric("fluid_mixer", FluidMixer::new);
    
    public static final MachineHolder<GenericMachineMenu, Disenchanter, MachineFourWayBlock<Disenchanter>, MachineBlockItem>
            DISENCHANTER = MACHINES.registerGeneric("disenchanter", Disenchanter::new);
    
    public static final MachineHolder<GenericMachineMenu, MobHarvester, MachineFourWayBlock<MobHarvester>, MachineBlockItem>
            MOB_HARVESTER = MACHINES.registerGeneric("mob_harvester", MobHarvester::new);
    
    public static final MachineHolder<GenericMachineMenu, MarineFisher, MachineFourWayBlock<MarineFisher>, MachineBlockItem>
            MARINE_FISHER = MACHINES.registerGeneric("marine_fisher", MarineFisher::new);
    
    public static final MachineHolder<GenericMachineMenu, Smithinator, MachineFourWayBlock<Smithinator>, MachineBlockItem>
            SMITHINATOR = MACHINES.registerGeneric("smithinator", Smithinator::new);
    
    public static final MachineHolder<GenericMachineMenu, DimensionalAnchor, MachineFourWayBlock<DimensionalAnchor>, MachineBlockItem>
            DIMENSIONAL_ANCHOR = MACHINES.registerGeneric("dimensional_anchor", DimensionalAnchor::new);
    
    public static final MachineHolder<GenericMachineMenu, Repairer, MachineFourWayBlock<Repairer>, MachineBlockItem>
            REPAIRER = MACHINES.registerGeneric("repairer", Repairer::new);
    
    public static final MachineHolder<GenericMachineMenu, Recycler, MachineFourWayBlock<Recycler>, MachineBlockItem>
            RECYCLER = MACHINES.registerGeneric("recycler", Recycler::new);
    
    public static final MachineHolder<GenericMachineMenu, Packer, MachineFourWayBlock<Packer>, MachineBlockItem>
            PACKER = MACHINES.registerGeneric("packer", Packer::new);
    
    public static final MachineHolder<GenericMachineMenu, Unpacker, MachineFourWayBlock<Unpacker>, MachineBlockItem>
            UNPACKER = MACHINES.registerGeneric("unpacker", Unpacker::new);
    
    public static final MachineHolder<GenericMachineMenu, SludgeRefiner, MachineFourWayBlock<SludgeRefiner>, MachineBlockItem>
            SLUDGE_REFINER = MACHINES.registerGeneric("sludge_refiner", SludgeRefiner::new);
    
    public static final MachineHolder<GenericMachineMenu, FluidPump, MachineFourWayBlock<FluidPump>, MachineBlockItem>
            FLUID_PUMP = MACHINES.registerGeneric("fluid_pump", FluidPump::new);
    
    public static final MachineHolder<GenericMachineMenu, BookDuplicator, MachineFourWayBlock<BookDuplicator>, MachineBlockItem>
            BOOK_DUPLICATOR = MACHINES.registerGeneric("book_duplicator", BookDuplicator::new);
    
    public static final MachineHolder<GenericMachineMenu, BatteryBox, MachineFourWayBlock<BatteryBox>, MachineBlockItem>
            BATTERY_BOX = MACHINES.register(MachineRegistration.generic("battery_box", BatteryBox::new)
                    .uniqueTexture()
                    .build());

    // ==================== Standard Machines ====================
    public static final MachineHolder<ElectricFurnaceMenu, ElectricFurnace, MachineFourWayBlock<ElectricFurnace>, MachineBlockItem>
            ELECTRIC_FURNACE = MACHINES.register(MachineRegistration
                    .standard("electric_furnace", ElectricFurnaceMenu::new, ElectricFurnace::new)
                    .build());

    public static final MachineHolder<AutoCutterMenu, AutoCutter, MachineFourWayBlock<AutoCutter>, MachineBlockItem>
            AUTOCUTTER = MACHINES.register(MachineRegistration
                    .standard("autocutter", AutoCutterMenu::new, AutoCutter::new)
                    .dataSize(3)
                    .build());

    public static final MachineHolder<GenericMachineMenu, OilRefinery, OilRefineryBlock<OilRefinery>, BewlrMachineItem>
            OIL_REFINERY = MACHINES.register(MachineRegistration
                    .standard("oil_refinery", GenericMachineMenu::new, OilRefinery::new)
                    .block(OilRefineryBlock::new)
                    .item(BewlrMachineItem::new)
                    .uniqueTexture()
                    .customRender()
                    .build());

    public static final MachineHolder<AdvancedCrafterMenu, AdvancedCrafter, MachineFourWayBlock<AdvancedCrafter>, MachineBlockItem>
            ADVANCED_CRAFTER = MACHINES.register(MachineRegistration
                    .standard("advanced_crafter", AdvancedCrafterMenu::new, AdvancedCrafter::new)
                    .build());

    public static final MachineHolder<InductionFurnaceMenu, InductionFurnace, MachineFourWayBlock<InductionFurnace>, MachineBlockItem>
            INDUCTION_FURNACE = MACHINES.register(MachineRegistration
                    .standard("induction_furnace", InductionFurnaceMenu::new, InductionFurnace::new)
                    .build());

    public static final MachineHolder<AlchemicalBrewerMenu, AlchemicalBrewer, MachineFourWayBlock<AlchemicalBrewer>, MachineBlockItem>
            ALCHEMICAL_BREWER = MACHINES.register(MachineRegistration
                    .standard("alchemical_brewer", AlchemicalBrewerMenu::new, AlchemicalBrewer::new)
                    .build());

    public static final MachineHolder<BottlerMenu, Bottler, MachineFourWayBlock<Bottler>, MachineBlockItem>
            BOTTLER = MACHINES.register(MachineRegistration
                    .standard("bottler", BottlerMenu::new, Bottler::new)
                    .build());

    public static final MachineHolder<HarvesterMenu, Harvester, MachineFourWayBlock<Harvester>, MachineBlockItem>
            HARVESTER = MACHINES.register(MachineRegistration
                    .standard("harvester", HarvesterMenu::new, Harvester::new)
                    .build());

    public static final MachineHolder<AnvilinatorMenu, Anvilinator, MachineFourWayBlock<Anvilinator>, MachineBlockItem>
            ANVILINATOR = MACHINES.register(MachineRegistration
                    .standard("anvilinator", AnvilinatorMenu::new, Anvilinator::new)
                    .build());

    // ==================== Six-Way Machines ====================
    public static final MachineHolder<BlockInteractorMenu, BlockPlacer, MachineSixWayBlock<BlockPlacer>, MachineBlockItem>
            BLOCK_PLACER = MACHINES.register(MachineRegistration
                    .sixWay("block_placer", BlockInteractorMenu::new, BlockPlacer::new)
                    .build());

    public static final MachineHolder<BlockInteractorMenu, BlockBreaker, MachineSixWayBlock<BlockBreaker>, MachineBlockItem>
            BLOCK_BREAKER = MACHINES.register(MachineRegistration
                    .sixWay("block_breaker", BlockInteractorMenu::new, BlockBreaker::new)
                    .build());
}