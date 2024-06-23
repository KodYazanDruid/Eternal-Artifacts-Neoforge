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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MeatPackerMachineBlockEntity>> MEAT_PACKER = BLOCK_ENTITIES.register("meat_packer", ()->
            BlockEntityType.Builder.of(MeatPackerMachineBlockEntity::new, ModBlocks.MEAT_PACKER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MeatShredderMachineBlockEntity>> MEAT_SHREDDER = BLOCK_ENTITIES.register("meat_shredder", ()->
            BlockEntityType.Builder.of(MeatShredderMachineBlockEntity::new, ModBlocks.MEAT_SHREDDER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX = BLOCK_ENTITIES.register("battery_box", ()->
            BlockEntityType.Builder.of(BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MobLiquifierMachineBlockEntity>> MOB_LIQUIFIER = BLOCK_ENTITIES.register("mob_liquifier", ()->
            BlockEntityType.Builder.of(MobLiquifierMachineBlockEntity::new, ModBlocks.MOB_LIQUIFIER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidCombustionDynamoBlockEntity>> FLUID_COMBUSTION_DYNAMO = BLOCK_ENTITIES.register("fluid_combustion_dynamo", ()->
            BlockEntityType.Builder.of(FluidCombustionDynamoBlockEntity::new, ModBlocks.FLUID_COMBUSTION_DYNAMO.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioFurnaceEntity>> BIOFURNACE = BLOCK_ENTITIES.register("biofurnace", ()->
            BlockEntityType.Builder.of(BioFurnaceEntity::new, ModBlocks.BIOFURNACE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonatorBlockEntity>> RESONATOR = BLOCK_ENTITIES.register("resonator", ()->
            BlockEntityType.Builder.of(ResonatorBlockEntity::new, ModBlocks.RESONATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GardeningPotEntity>> GARDENING_POT = BLOCK_ENTITIES.register("gardening_pot", () ->
            BlockEntityType.Builder.of(GardeningPotEntity::new, ModBlocks.GARDENING_POT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<JarBlockEntity>> JAR = BLOCK_ENTITIES.register("jar", () ->
            BlockEntityType.Builder.of(JarBlockEntity::new, ModBlocks.JAR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FancyChestBlockEntity>> FANCY_CHEST = BLOCK_ENTITIES.register("fancy_chest", () ->
            BlockEntityType.Builder.of(FancyChestBlockEntity::new, ModBlocks.FANCY_CHEST.get()).build(null));

}
