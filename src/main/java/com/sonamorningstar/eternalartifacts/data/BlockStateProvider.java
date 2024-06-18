package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.content.block.AncientCropBlock;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3;


public class BlockStateProvider extends net.neoforged.neoforge.client.model.generators.BlockStateProvider {
    private final PackOutput output;
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return super.run(cache);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.BIOFURNACE.get());
        simpleBlockWithItem(ModBlocks.SUGAR_CHARCOAL_BLOCK.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_COAL_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_COPPER_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_IRON_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_GOLD_ORE.get());
        simpleBlockWithItem(ModBlocks.CHLOROPHYTE_DEBRIS.get());
        simpleBlockWithItem(ModBlocks.SANDY_TILED_STONE_BRICKS.get());
        simpleBlockWithItem(ModBlocks.SANDY_STONE_BRICKS.get());
        simpleBlockWithItem(ModBlocks.SANDY_PRISMARINE.get());
        simpleBlockWithItem(ModBlocks.VERY_SANDY_PRISMARINE.get());
        simpleBlockWithItem(ModBlocks.SANDY_DARK_PRISMARINE.get());
        simpleBlockWithItem(ModBlocks.VERY_SANDY_DARK_PRISMARINE.get());
        simpleBlockWithItem(ModBlocks.PAVED_PRISMARINE_BRICKS.get());
        simpleBlockWithItem(ModBlocks.SANDY_PAVED_PRISMARINE_BRICKS.get());
        simpleBlockWithItem(ModBlocks.SANDY_PRISMARINE_BRICKS.get());
        simpleBlockWithItem(ModBlocks.LAYERED_PRISMARINE.get());
        simpleBlockWithItem(ModBlocks.CITRUS_PLANKS.get());
        axisBlock(ModBlocks.ROSY_FROGLIGHT.get(), modLoc("block/rosy_froglight_side"), modLoc("block/rosy_froglight_top"));
        axisBlock(ModBlocks.CITRUS_LOG.get(), modLoc("block/citrus_log"), modLoc("block/citrus_log_top"));
        axisBlock(ModBlocks.STRIPPED_CITRUS_LOG.get(), modLoc("block/stripped_citrus_log"), modLoc("block/stripped_citrus_log_top"));
        axisBlock(ModBlocks.CITRUS_WOOD.get(), modLoc("block/citrus_log"), modLoc("block/citrus_log"));
        axisBlock(ModBlocks.STRIPPED_CITRUS_WOOD.get(), modLoc("block/stripped_citrus_log"), modLoc("block/stripped_citrus_log"));

        simpleBlock(ModBlocks.PINK_SLIME_BLOCK.get(),
            ConfiguredModel.builder().modelFile(
                models().withExistingParent("pink_slime_block", mcLoc("block/slime_block"))
                    .texture("texture", modLoc("block/pink_slime_block"))
                    .texture("particle", modLoc("block/pink_slime_block"))
                    .renderType(mcLoc("translucent"))).build());

        simpleBlock(ModBlocks.MACHINE_BLOCK.get(),
            ConfiguredModel.builder().modelFile(
                models().cube(ModBlocks.MACHINE_BLOCK.getId().getPath(),
                        modLoc("block/machine_bottom"),
                        modLoc("block/machine_top"),
                        modLoc("block/machine_side"),
                        modLoc("block/machine_side"),
                        modLoc("block/machine_side"),
                        modLoc("block/machine_side")
                ).texture("particle", modLoc("block/machine_side"))
            ).build());

        simpleBlock(ModBlocks.JAR.get(), ConfiguredModel.builder().modelFile(new ModelFile.ExistingModelFile(modLoc("block/jar"), models().existingFileHelper)).build());

        machineBlock(ModBlocks.ANVILINATOR, false);
        machineBlock(ModBlocks.BOOK_DUPLICATOR, false);
        machineBlock(ModBlocks.MEAT_PACKER, false);
        machineBlock(ModBlocks.MEAT_SHREDDER, false);
        machineBlock(ModBlocks.BATTERY_BOX, true);
        machineBlock(ModBlocks.MOB_LIQUIFIER, false);

        directionBlock(ModBlocks.RESONATOR.get(), (state, builder) ->
                builder.modelFile(new ModelFile.ExistingModelFile(modLoc("block/resonator"), models().existingFileHelper)),BlockStateProperties.FACING);

        directionBlock(ModBlocks.FANCY_CHEST.get(), (state, builder) ->
                builder.modelFile(new ModelFile.ExistingModelFile(modLoc("block/fancy_chest"), models().existingFileHelper)),BlockStateProperties.HORIZONTAL_FACING);

        simpleBlock(ModBlocks.GARDENING_POT.get(), new ModelFile.ExistingModelFile(modLoc("block/gardening_pot"), models().existingFileHelper));

        makeAncientCrop(ModBlocks.ANCIENT_CROP.get(), "ancient_crop");
        tallFlower(ModBlocks.FORSYTHIA);
        tintedCrossBlock(ModBlocks.FOUR_LEAF_CLOVER);

        createOreBerries(ModBlocks.COPPER_ORE_BERRY);
        createOreBerries(ModBlocks.IRON_ORE_BERRY);
        createOreBerries(ModBlocks.GOLD_ORE_BERRY);
        createOreBerries(ModBlocks.EXPERIENCE_ORE_BERRY);
        createOreBerries(ModBlocks.MANGANESE_ORE_BERRY);
    }

    private void machineBlock(DeferredBlock<? extends Block> holder, boolean unique) {
        String name = holder.getId().getPath();
        String top = unique ? "block/"+name+"_top" : "block/machine_top";
        String bottom = unique ?"block/"+name+"_bottom" : "block/machine_bottom";
        String siding = unique ? "block/"+name+"_side" : "block/machine_side";
        directionBlock(holder.get(), (state, builder) ->
            builder.modelFile(models().cube(name,
                modLoc(bottom),
                modLoc(top),
                modLoc("block/"+name+"_front"),
                modLoc(siding),
                modLoc(siding),
                modLoc(siding))
            .texture("particle", modLoc(siding))), BlockStateProperties.HORIZONTAL_FACING);
        simpleBlockItem(holder.get(), models().getExistingFile(modLoc("block/"+name)));
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

    private void tallFlower(DeferredBlock<TallFlowerBlock> flower) {
        String name = flower.getId().getPath();
        getVariantBuilder(flower.get()).forAllStates(state -> {
            DoubleBlockHalf half = state.getValue(DoublePlantBlock.HALF);
            String partName = name + '_' + half.getSerializedName();
            ModelFile model = models().cross(partName, modLoc("block/"+partName)).renderType("cutout");
            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void crossBlock(DeferredBlock<? extends Block> block) {
        ModelFile model = models()
            .cross(block.getId().getPath(), blockTexture(block.get()))
            .renderType("cutout");

        simpleBlock(block.get(), model);
    }

    private void tintedCrossBlock(DeferredBlock<? extends Block> block) {
        ModelFile model = models()
            .withExistingParent(block.getId().getPath(), mcLoc("block/tinted_cross"))
            .texture("cross", "block/"+block.getId().getPath())
            .renderType("cutout");
        simpleBlock(block.get(), model);
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

    private void createOreBerries(DeferredBlock<?> deferred) {
        VariantBlockStateBuilder cobVariant = getVariantBuilder(deferred.get());
        cobVariant.forAllStates(state -> {
            int age = state.getValue(AGE_3);
            String path = BuiltInRegistries.BLOCK.getKey(deferred.get()).getPath();
            String texture = age == 3 ? "block/"+path+"_ripe" : "block/"+path;
            ModelFile model;
            switch(age) {
                case 0 -> model = models().withExistingParent(path+"_stage0", new ResourceLocation(MODID, "block/cube4")).texture("all", "block/"+path).renderType("cutout");
                case 1 -> model = models().withExistingParent(path+"_stage1", new ResourceLocation(MODID, "block/cube10")).texture("all", "block/"+path).renderType("cutout");
                default -> model = models().withExistingParent(path+"_stage"+age,mcLoc("block/cube_all")).texture("all", texture).renderType("cutout");
            }
            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void simpleBlockWithItem(Block block) {
        ModelFile model = cubeAll(block);
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

}
