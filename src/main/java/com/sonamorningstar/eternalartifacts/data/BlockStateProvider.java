package com.sonamorningstar.eternalartifacts.data;

import com.mojang.datafixers.types.Func;
import com.sonamorningstar.eternalartifacts.content.block.AncientCropBlock;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;


public class BlockStateProvider extends net.neoforged.neoforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //simpleBlockWithItem(ModBlocks.LUTFI.get());
        simpleBlockWithItem(ModBlocks.BIOFURNACE.get());

        directionBlock(ModBlocks.ANVILINATOR.get(), (state, builder) ->
                builder.modelFile(models().cube(ModBlocks.ANVILINATOR.getId().getPath(), modLoc("block/anvilinator_down"), modLoc("block/anvilinator_up"), modLoc("block/anvilinator_front"), modLoc("block/anvilinator_side"), modLoc("block/anvilinator_side"), modLoc("block/anvilinator_side"))
                        .texture("particle", modLoc("block/anvilinator_side"))), BlockStateProperties.HORIZONTAL_FACING);

        directionBlock(ModBlocks.RESONATOR.get(), (state, builder) ->
                builder.modelFile(new ModelFile.ExistingModelFile(modLoc("block/resonator"), models().existingFileHelper)),BlockStateProperties.FACING);

        simpleBlock(ModBlocks.GARDENING_POT.get(), new ModelFile.ExistingModelFile(modLoc("block/gardening_pot"), models().existingFileHelper));

        makeAncientCrop(ModBlocks.ANCIENT_CROP.get(), "ancient_crop");


    }

    private void makeAncientCrop(CropBlock crop, String textureName) {
        Function<BlockState, ConfiguredModel[]> func = state -> ancientCropStates(state, crop, textureName);

        getVariantBuilder(crop).forAllStates(func);
    }

    private ConfiguredModel[] ancientCropStates(BlockState state, CropBlock crop, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(textureName + "_" + state.getValue(((AncientCropBlock) crop).getAgeProperty()),
                modLoc("block/" + textureName + "_" + state.getValue(((AncientCropBlock) crop).getAgeProperty()))).renderType("cutout"));
        return models;
    }

    private VariantBlockStateBuilder directionBlock(Block block, BiConsumer<BlockState, ConfiguredModel.Builder<?>> model, Property<Direction> direction) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        builder.forAllStates(state -> {
            ConfiguredModel.Builder<?> bld = ConfiguredModel.builder();
            model.accept(state, bld);
            applyRotation(bld, state.getValue(direction));
            return bld.build();
        });
        return builder;
    }

    private void applyRotation(ConfiguredModel.Builder<?> builder, Direction direction) {
        switch (direction) {
            case UP -> builder.rotationX(-90);
            case DOWN -> builder.rotationX(90);
            case NORTH -> { }
            case EAST -> builder.rotationY(90);
            case SOUTH -> builder.rotationY(180);
            case WEST -> builder.rotationY(270);
        }
    }

    private void simpleBlockWithItem(Block block) {
        ModelFile model = cubeAll(block);
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

}
