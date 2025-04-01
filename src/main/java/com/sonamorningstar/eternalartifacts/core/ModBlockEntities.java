package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnvilinatorBlockEntity>> ANVILINATOR = BLOCK_ENTITIES.register("anvilinator", ()->
            BlockEntityType.Builder.of(AnvilinatorBlockEntity::new, ModBlocks.ANVILINATOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BookDuplicatorMachineBlockEntity>> BOOK_DUPLICATOR = BLOCK_ENTITIES.register("book_duplicator", ()->
            BlockEntityType.Builder.of(BookDuplicatorMachineBlockEntity::new, ModBlocks.BOOK_DUPLICATOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX = BLOCK_ENTITIES.register("battery_box", ()->
            BlockEntityType.Builder.of(BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidCombustionDynamoBlockEntity>> FLUID_COMBUSTION_DYNAMO = BLOCK_ENTITIES.register("fluid_combustion_dynamo", ()->
            BlockEntityType.Builder.of(FluidCombustionDynamoBlockEntity::new, ModBlocks.FLUID_COMBUSTION_DYNAMO.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NousTankBlockEntity>> NOUS_TANK = BLOCK_ENTITIES.register("nous_tank", ()->
            BlockEntityType.Builder.of(NousTankBlockEntity::new, ModBlocks.NOUS_TANK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioFurnaceEntity>> BIOFURNACE = BLOCK_ENTITIES.register("biofurnace", ()->
            BlockEntityType.Builder.of(BioFurnaceEntity::new, ModBlocks.BIOFURNACE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDockBlockEntity>> ENERGY_DOCK = BLOCK_ENTITIES.register("energy_dock", ()->
            BlockEntityType.Builder.of(EnergyDockBlockEntity::new, ModBlocks.ENERGY_DOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShockAbsorberBlockEntity>> SHOCK_ABSORBER = BLOCK_ENTITIES.register("shock_absorber", ()->
            BlockEntityType.Builder.of(ShockAbsorberBlockEntity::new, ModBlocks.SHOCK_ABSORBER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TesseractBlockEntity>> TESSERACT = BLOCK_ENTITIES.register("tesseract", ()->
        BlockEntityType.Builder.of(TesseractBlockEntity::new, ModBlocks.TESSERACT.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonatorBlockEntity>> RESONATOR = BLOCK_ENTITIES.register("resonator", ()->
            BlockEntityType.Builder.of(ResonatorBlockEntity::new, ModBlocks.RESONATOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DrumBlockEntity>> DRUM = BLOCK_ENTITIES.register("drum", ()->
            BlockEntityType.Builder.of(DrumBlockEntity::new,
                    ModBlocks.COPPER_DRUM.get(),
                    ModBlocks.IRON_DRUM.get(),
                    ModBlocks.GOLD_DRUM.get(),
                    ModBlocks.STEEL_DRUM.get(),
                    ModBlocks.DIAMOND_DRUM.get(),
                    ModBlocks.NETHERITE_DRUM.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CableBlockEntity>> CABLE = BLOCK_ENTITIES.register("cable", ()->
            BlockEntityType.Builder.of(CableBlockEntity::new,
                    ModBlocks.COPPER_CABLE.get(),
                    ModBlocks.COVERED_COPPER_CABLE.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GardeningPotEntity>> GARDENING_POT = BLOCK_ENTITIES.register("gardening_pot", () ->
            BlockEntityType.Builder.of(GardeningPotEntity::new, ModBlocks.GARDENING_POT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<JarBlockEntity>> JAR = BLOCK_ENTITIES.register("jar", () ->
            BlockEntityType.Builder.of(JarBlockEntity::new, ModBlocks.JAR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BluePlasticCauldronBlockEntity>> BLUE_PLASTIC_CAULDRON = BLOCK_ENTITIES.register("blue_plastic_cauldron", () ->
            BlockEntityType.Builder.of(BluePlasticCauldronBlockEntity::new, ModBlocks.BLUE_PLASTIC_CAULDRON.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FancyChestBlockEntity>> FANCY_CHEST = BLOCK_ENTITIES.register("fancy_chest", () ->
            BlockEntityType.Builder.of(FancyChestBlockEntity::new, ModBlocks.FANCY_CHEST.get()).build(null));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MachineWorkbenchBlockEntity>> MACHINE_WORKBENCH = BLOCK_ENTITIES.register("machine_workbench", () ->
            BlockEntityType.Builder.of(MachineWorkbenchBlockEntity::new, ModBlocks.MACHINE_WORKBENCH.get()).build(null));
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ModSkullBlockEntity>> SKULL = BLOCK_ENTITIES.register("skull", () ->
            BlockEntityType.Builder.of(ModSkullBlockEntity::new,
                ModBlocks.DROWNED_HEAD.get(),
                ModBlocks.DROWNED_WALL_HEAD.get(),
                ModBlocks.HUSK_HEAD.get(),
                ModBlocks.HUSK_WALL_HEAD.get(),
                ModBlocks.STRAY_SKULL.get(),
                ModBlocks.STRAY_WALL_SKULL.get(),
                ModBlocks.BLAZE_HEAD.get(),
                ModBlocks.BLAZE_WALL_HEAD.get()
            ).build(null));

}
