package com.sonamorningstar.eternalartifacts.data;

import com.google.gson.JsonElement;
import com.sonamorningstar.eternalartifacts.content.block.BluePlasticCauldronBlock;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

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
                        TextureMapping.cauldron(TextureMapping.getBlockTexture(ModBlocks.LIQUID_PLASTIC_BLOCK.get(), "_still")), modelOutput)
        ));

        createBluePlasticCauldron(ModBlocks.BLUE_PLASTIC_CAULDRON.get(), new ResourceLocation(MODID, "block/blue_plastic"));

        blockEntityModels(ModBlocks.JAR.get(), Blocks.GLASS).create(ModBlocks.JAR.get());
        blockEntityModels(ModBlocks.NOUS_TANK.get(), Blocks.GLASS).create(ModBlocks.NOUS_TANK.get());
        blockEntityModels(ModBlocks.OIL_REFINERY.get(), Blocks.GLASS).create(ModBlocks.OIL_REFINERY.get());

        stateOutput.accept(
                createSimpleBlock(
                        ModBlocks.FLUID_COMBUSTION_DYNAMO.get(),
                        ModelTemplates.PARTICLE_ONLY.create(ModBlocks.FLUID_COMBUSTION_DYNAMO.get(), TextureMapping.particle(new ResourceLocation(MODID, "block/machine_side")), modelOutput)
                ));
    }

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

    private static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation modelLoc) {
        return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLoc));
    }

    private void createNonTemplateModelBlock(Block pBlock) {
        createNonTemplateModelBlock(pBlock, pBlock);
    }

    private void createNonTemplateModelBlock(Block pBlock, Block pModelBlock) {
        stateOutput.accept(createSimpleBlock(pBlock, ModelLocationUtils.getModelLocation(pModelBlock)));
    }

    private net.minecraft.data.models.BlockModelGenerators.BlockEntityModelGenerator blockEntityModels(ResourceLocation pEntityBlockModelLocation, Block pParticleBlock) {
        return new net.minecraft.data.models.BlockModelGenerators.BlockEntityModelGenerator(pEntityBlockModelLocation, pParticleBlock);
    }

    private net.minecraft.data.models.BlockModelGenerators.BlockEntityModelGenerator blockEntityModels(Block pEntityBlockBaseModel, Block pParticleBlock) {
        return new net.minecraft.data.models.BlockModelGenerators.BlockEntityModelGenerator(ModelLocationUtils.getModelLocation(pEntityBlockBaseModel), pParticleBlock);
    }
}
