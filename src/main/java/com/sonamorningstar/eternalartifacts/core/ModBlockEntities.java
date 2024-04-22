package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.entity.AnvilinatorBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.BioFurnaceEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.GardeningPotEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.ResonatorBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioFurnaceEntity>> BIOFURNACE = BLOCK_ENTITIES.register("biofurnace", ()->
            BlockEntityType.Builder.of(BioFurnaceEntity::new, ModBlocks.BIOFURNACE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonatorBlockEntity>> RESONATOR = BLOCK_ENTITIES.register("resonator", ()->
            BlockEntityType.Builder.of(ResonatorBlockEntity::new, ModBlocks.RESONATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GardeningPotEntity>> GARDENING_POT = BLOCK_ENTITIES.register("gardening_pot", () ->
            BlockEntityType.Builder.of(GardeningPotEntity::new, ModBlocks.GARDENING_POT.get()).build(null));

}
