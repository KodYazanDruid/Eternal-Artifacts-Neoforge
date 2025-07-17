package com.sonamorningstar.eternalartifacts.data;

import com.google.gson.JsonElement;
import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import com.sonamorningstar.eternalartifacts.content.block.BluePlasticCauldronBlock;
import com.sonamorningstar.eternalartifacts.content.block.PunjiBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.AttachmentablePipeBlock;
import com.sonamorningstar.eternalartifacts.content.block.properties.PipeConnectionProperty;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class BlockModelGenerators extends net.minecraft.data.models.BlockModelGenerators {
    private final Consumer<BlockStateGenerator> stateOutput;
    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;
    private final Consumer<Item> skipped;

    public BlockModelGenerators(Consumer<BlockStateGenerator> stateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, Consumer<Item> skipped) {
        super(stateOutput, modelOutput, skipped);
        this.stateOutput = stateOutput;
        this.modelOutput = modelOutput;
        this.skipped = skipped;
    }

    @Override
    public void run() {
        stateOutput.accept(createSimpleBlock(ModBlocks.PLASTIC_CAULDRON.get(),
            ModelTemplates.CAULDRON_FULL
                .create(ModBlocks.PLASTIC_CAULDRON.get(),
                        TextureMapping.cauldron(TextureMapping.getBlockTexture(ModFluids.LIQUID_PLASTIC.getFluidBlock(), "_still")), modelOutput)
        ));

        createBluePlasticCauldron(ModBlocks.BLUE_PLASTIC_CAULDRON.get(), new ResourceLocation(MODID, "block/blue_plastic"));

        createForParticle(ModBlocks.JAR, Blocks.GLASS);
        createForParticle(ModBlocks.NOUS_TANK, Blocks.GLASS);
        createForParticle(ModMachines.OIL_REFINERY.getBlockHolder(), Blocks.GLASS);
        createForParticle(ModBlocks.ENERGY_DOCK, new ResourceLocation(MODID, "block/machine_side"));
        createForParticle(ModBlocks.FLUID_COMBUSTION_DYNAMO, new ResourceLocation(MODID, "block/machine_side"));
        createForParticle(ModBlocks.SOLID_COMBUSTION_DYNAMO, new ResourceLocation(MODID, "block/machine_side"));
        createForParticle(ModBlocks.DROWNED_HEAD, Blocks.SOUL_SAND);
        createForParticle(ModBlocks.DROWNED_WALL_HEAD, Blocks.SOUL_SAND);
        createForParticle(ModBlocks.HUSK_HEAD, Blocks.SOUL_SAND);
        createForParticle(ModBlocks.HUSK_WALL_HEAD, Blocks.SOUL_SAND);
        createForParticle(ModBlocks.STRAY_SKULL, Blocks.SOUL_SAND);
        createForParticle(ModBlocks.STRAY_WALL_SKULL, Blocks.SOUL_SAND);
        createForParticle(ModBlocks.BLAZE_HEAD, Blocks.SOUL_SAND);
        createForParticle(ModBlocks.BLAZE_WALL_HEAD, Blocks.SOUL_SAND);

        createPunjiStick(ModBlocks.PUNJI_STICKS.get());

        cable(ModBlocks.TIN_CABLE.get(), false);
        cable(ModBlocks.COVERED_TIN_CABLE.get(), true);
        cable(ModBlocks.COPPER_CABLE.get(), false);
        cable(ModBlocks.COVERED_COPPER_CABLE.get(), true);
        cable(ModBlocks.GOLD_CABLE.get(), false);
        cable(ModBlocks.COVERED_GOLD_CABLE.get(), true);
        
        pipe(ModBlocks.COPPER_FLUID_PIPE.get());
        pipe(ModBlocks.GOLD_FLUID_PIPE.get());
        pipe(ModBlocks.STEEL_FLUID_PIPE.get());
        
        pipe(ModBlocks.COPPER_ITEM_PIPE.get());
        pipe(ModBlocks.GOLD_ITEM_PIPE.get());
        pipe(ModBlocks.STEEL_ITEM_PIPE.get());
        
        createNormalTorch(ModBlocks.GLOWSTONE_TORCH.get(), ModBlocks.GLOWSTONE_WALL_TORCH.get());
        createNormalTorch(ModBlocks.GLOWTORCH.get(), ModBlocks.WALL_GLOWTORCH.get());
        
        createSolarPanel();
        ModBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach(family -> family(family.getBaseBlock()).generateFor(family));
    }
    //region Functions for block models...
    private void createBluePlasticCauldron(Block block, ResourceLocation layerTex) {
        stateOutput
            .accept(
                MultiVariantGenerator.multiVariant(block)
                    .with(
                        PropertyDispatch.property(BluePlasticCauldronBlock.LEVEL)
                            .select(
                                1,
                                Variant.variant()
                                    .with(
                                        VariantProperties.MODEL,
                                        ModelTemplates.CAULDRON_LEVEL1
                                            .createWithSuffix(
                                                block,
                                                "_level1",
                                                TextureMapping.cauldron(layerTex),
                                                modelOutput
                                            )
                                    )
                            )
                            .select(
                                2,
                                Variant.variant()
                                    .with(
                                        VariantProperties.MODEL,
                                        ModelTemplates.CAULDRON_LEVEL2
                                            .createWithSuffix(
                                                block,
                                                "_level2",
                                                TextureMapping.cauldron(layerTex),
                                                modelOutput
                                            )
                                    )
                            )
                            .select(
                                3,
                                Variant.variant()
                                    .with(
                                        VariantProperties.MODEL,
                                        ModelTemplates.CAULDRON_FULL
                                            .createWithSuffix(
                                                block,
                                                "_full",
                                                TextureMapping.cauldron(layerTex),
                                                modelOutput
                                            )
                                    )
                            )
                    )
            );


    }

    private void createPunjiStick(Block block) {
        createSimpleFlatItemModel(block.asItem());
        ModelLocationUtils.getModelLocation(ModBlocks.PUNJI_STICKS.get(), "_one");
        stateOutput.accept(MultiVariantGenerator.multiVariant(block).with(
            PropertyDispatch.property(PunjiBlock.STICKS)
                .select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.PUNJI_STICKS.get(), "_one")))
                .select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.PUNJI_STICKS.get(), "_two")))
                .select(3, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.PUNJI_STICKS.get(), "_three")))
                .select(4, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.PUNJI_STICKS.get(), "_four")))
                .select(5, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(ModBlocks.PUNJI_STICKS.get(), "_five")))
        ));
    }

    public void cable(Block cable, boolean isCovered) {
        ResourceLocation inside;
        ResourceLocation side;
        ResourceLocation inventory;
        if (isCovered) {
            inside = ModModelTemplates.COVERED_CABLE_INSIDE.create(cable, ModTextureMappings.pipe(cable), modelOutput);
            side = ModModelTemplates.COVERED_CABLE_SIDE.create(cable, ModTextureMappings.pipe(cable), modelOutput);
            inventory = ModModelTemplates.COVERED_CABLE_INVENTORY.create(cable, ModTextureMappings.pipe(cable), modelOutput);
        } else {
            inside = ModModelTemplates.CABLE_INSIDE.create(cable, ModTextureMappings.pipe(cable), modelOutput);
            side = ModModelTemplates.CABLE_SIDE.create(cable, ModTextureMappings.pipe(cable), modelOutput);
            inventory = ModModelTemplates.CABLE_INVENTORY.create(cable, ModTextureMappings.pipe(cable), modelOutput);
        }
        stateOutput.accept(createCable(cable, inside, side));
        delegateItemModel(cable, inventory);
    }
    public void pipe(Block pipe) {
        ResourceLocation inside = ModModelTemplates.PIPE_INSIDE.create(pipe, ModTextureMappings.pipe(pipe), modelOutput);
        ResourceLocation side = ModModelTemplates.PIPE_SIDE.create(pipe, ModTextureMappings.pipe(pipe), modelOutput);
        ResourceLocation sideExtract = ModModelTemplates.PIPE_SIDE_EXTRACTING.create(pipe, ModTextureMappings.pipe(pipe), modelOutput);
        ResourceLocation sideFilter = ModModelTemplates.PIPE_SIDE_FILTERING.create(pipe, ModTextureMappings.pipe(pipe), modelOutput);
        ResourceLocation inventory = ModModelTemplates.PIPE_INVENTORY.create(pipe, ModTextureMappings.pipe(pipe), modelOutput);
        stateOutput.accept(createPipe(pipe, inside, side, sideExtract, sideFilter));
        delegateItemModel(pipe, inventory);
    }

    private BlockStateGenerator createCable(Block cable, ResourceLocation insideLoc, ResourceLocation sideLoc) {
        return MultiPartGenerator.multiPart(cable)
            .with(Variant.variant().with(VariantProperties.MODEL, insideLoc))
            .with(
                Condition.condition().term(CableBlock.NORTH, true),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
            )
            .with(
                Condition.condition().term(CableBlock.EAST, true),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
            )
            .with(
                Condition.condition().term(CableBlock.SOUTH, true),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
            )
            .with(
                Condition.condition().term(CableBlock.WEST, true),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
            )
            .with(
                Condition.condition().term(CableBlock.UP, true),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
            )
            .with(
                Condition.condition().term(CableBlock.DOWN, true),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
            );
    }
    private BlockStateGenerator createPipe(Block cable, ResourceLocation insideLoc,
                                           ResourceLocation sideLoc, ResourceLocation sideExtract, ResourceLocation sideFilter) {
        return MultiPartGenerator.multiPart(cable)
            .with(Variant.variant().with(VariantProperties.MODEL, insideLoc))
            .with(
                Condition.condition().term(AttachmentablePipeBlock.NORTH_CONNECTION, PipeConnectionProperty.PipeConnection.FREE),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.NORTH_CONNECTION, PipeConnectionProperty.PipeConnection.EXTRACT),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideExtract)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.NORTH_CONNECTION, PipeConnectionProperty.PipeConnection.FILTERED),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideFilter)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.EAST_CONNECTION, PipeConnectionProperty.PipeConnection.FREE),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.EAST_CONNECTION, PipeConnectionProperty.PipeConnection.EXTRACT),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideExtract)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.EAST_CONNECTION, PipeConnectionProperty.PipeConnection.FILTERED),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideFilter)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.SOUTH_CONNECTION, PipeConnectionProperty.PipeConnection.FREE),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.SOUTH_CONNECTION, PipeConnectionProperty.PipeConnection.EXTRACT),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideExtract)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.SOUTH_CONNECTION, PipeConnectionProperty.PipeConnection.FILTERED),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideFilter)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.WEST_CONNECTION, PipeConnectionProperty.PipeConnection.FREE),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.WEST_CONNECTION, PipeConnectionProperty.PipeConnection.EXTRACT),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideExtract)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.WEST_CONNECTION, PipeConnectionProperty.PipeConnection.FILTERED),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideFilter)
                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.UP_CONNECTION, PipeConnectionProperty.PipeConnection.FREE),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.UP_CONNECTION, PipeConnectionProperty.PipeConnection.EXTRACT),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideExtract)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.UP_CONNECTION, PipeConnectionProperty.PipeConnection.FILTERED),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideFilter)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.DOWN_CONNECTION, PipeConnectionProperty.PipeConnection.FREE),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideLoc)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.DOWN_CONNECTION, PipeConnectionProperty.PipeConnection.EXTRACT),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideExtract)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
            )
            .with(
                Condition.condition().term(AttachmentablePipeBlock.DOWN_CONNECTION, PipeConnectionProperty.PipeConnection.FILTERED),
                Variant.variant()
                    .with(VariantProperties.MODEL, sideFilter)
                    .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
            );
    }
    
    private void createSolarPanel() {
        TextureMapping modelMapping = TextureMapping.cubeBottomTop(ModBlocks.SOLAR_PANEL.get());
        ResourceLocation bottom = ModelTemplates.SLAB_BOTTOM.create(ModBlocks.SOLAR_PANEL.get(), modelMapping, this.modelOutput);
        ResourceLocation top = ModelTemplates.SLAB_TOP.create(ModBlocks.SOLAR_PANEL.get(), modelMapping, this.modelOutput);
        ResourceLocation doubleSlab = ModelTemplates.CUBE_BOTTOM_TOP
            .createWithOverride(ModBlocks.SOLAR_PANEL.get(), "_double", modelMapping, this.modelOutput);
        stateOutput.accept(createSlab(ModBlocks.SOLAR_PANEL.get(), bottom, top, doubleSlab));
        delegateItemModel(ModBlocks.SOLAR_PANEL.get(), bottom);
    }
    //endregion
    void createSimpleFlatItemModel(Item item) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(item), modelOutput);
    }

    private void createForParticle(DeferredHolder<Block, ? extends Block> holder, Block particle) {
        createForParticle(holder, ModelLocationUtils.getModelLocation(particle));
    }
    private void createForParticle(DeferredHolder<Block, ? extends Block> holder, ResourceLocation particle) {
        stateOutput.accept(createSimpleBlock(holder.get(), ModelTemplates.PARTICLE_ONLY.create(holder.get(), TextureMapping.particle(particle), modelOutput)));
    }
}
