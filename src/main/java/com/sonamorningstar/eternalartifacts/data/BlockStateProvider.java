package com.sonamorningstar.eternalartifacts.data;

import com.sonamorningstar.eternalartifacts.content.block.AncientCropBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineFourWayBlock;
import com.sonamorningstar.eternalartifacts.content.block.base.MachineSixWayBlock;
import com.sonamorningstar.eternalartifacts.core.ModBlockFamilies;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModMachines;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

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
        simpleBlockWithItem(ModBlocks.GRAVEL_MANGANESE_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_COAL_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_COPPER_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_IRON_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_GOLD_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_DIAMOND_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_EMERALD_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_REDSTONE_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_LAPIS_ORE.get());
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
        simpleBlockWithItem(ModBlocks.MANGANESE_ORE.get());
        simpleBlockWithItem(ModBlocks.DEEPSLATE_MANGANESE_ORE.get());
        simpleBlockWithItem(ModBlocks.RAW_MANGANESE_BLOCK.get());
        simpleBlockWithItem(ModBlocks.CHARCOAL_BLOCK.get());
        simpleBlockWithItem(ModBlocks.MARIN_ORE.get());
        simpleBlockWithItem(ModBlocks.RAW_MARIN_BLOCK.get());
        simpleBlockWithItem(ModBlocks.MARIN_BLOCK.get());
        simpleBlockWithItemWithRenderType(ModBlocks.ICE_BRICKS.get(), "translucent");
        createSpecialSlab(ModBlockFamilies.ICE_BRICKS, "translucent");
        createSpecialStairs(ModBlockFamilies.ICE_BRICKS, "translucent");
        createSpecialWall(ModBlockFamilies.ICE_BRICKS, "translucent");
        simpleBlockWithItem(ModBlocks.ASPHALT_BLOCK.get());
        simpleBlockWithItem(ModBlocks.STEEL_BLOCK.get());
        simpleBlockWithItemWithRenderType(ModBlocks.TEMPERED_GLASS.get(), "cutout");
        simpleBlockWithItem(ModBlocks.DEMON_BLOCK.get());
        columnBlockWithItem(ModBlocks.SHOCK_ABSORBER);
        simpleBlockWithItem(ModBlocks.MOSS_MANGANESE_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_COAL_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_COPPER_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_IRON_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_GOLD_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_DIAMOND_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_EMERALD_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_REDSTONE_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_LAPIS_ORE.get());
        simpleBlockWithItem(ModBlocks.TIN_ORE.get());
        simpleBlockWithItem(ModBlocks.DEEPSLATE_TIN_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_TIN_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_TIN_ORE.get());
        simpleBlockWithItem(ModBlocks.ALUMINUM_ORE.get());
        simpleBlockWithItem(ModBlocks.DEEPSLATE_ALUMINUM_ORE.get());
        simpleBlockWithItem(ModBlocks.GRAVEL_ALUMINUM_ORE.get());
        simpleBlockWithItem(ModBlocks.MOSS_ALUMINUM_ORE.get());
        simpleBlockWithItem(ModBlocks.INDUSTRIAL_SPONGE.get());
        simpleBlockWithItem(ModBlocks.WET_INDUSTRIAL_SPONGE.get());
        deepStorageUnit(ModBlocks.DEEP_ITEM_STORAGE_UNIT);
        //deepStorageUnit(ModBlocks.DEEP_INFINITE_ITEM_STORAGE_UNIT);
        deepStorageUnit(ModBlocks.DEEP_FLUID_STORAGE_UNIT);
        //deepStorageUnit(ModBlocks.DEEP_INFINITE_FLUID_STORAGE_UNIT);
        
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

        createStateForModelWithProperty(ModBlocks.RESONATOR, BlockStateProperties.FACING);
        createStateForModelWithProperty(ModBlocks.FANCY_CHEST, BlockStateProperties.HORIZONTAL_FACING);
        createStateForModel(ModBlocks.GARDENING_POT);
        createStateForModel(ModBlocks.TESSERACT);
        createDrums(ModBlocks.COPPER_DRUM, Blocks.COPPER_BLOCK);
        createDrums(ModBlocks.IRON_DRUM, Blocks.IRON_BLOCK);
        createDrums(ModBlocks.GOLD_DRUM, Blocks.GOLD_BLOCK);
        createDrums(ModBlocks.STEEL_DRUM, ModBlocks.STEEL_BLOCK.get());
        createDrums(ModBlocks.DIAMOND_DRUM, Blocks.DIAMOND_BLOCK);
        createDrums(ModBlocks.NETHERITE_DRUM, Blocks.NETHERITE_BLOCK);
        createStateForModel(ModBlocks.TRASH_CAN);
        createStateForModelWithProperty(ModBlocks.MACHINE_WORKBENCH, BlockStateProperties.HORIZONTAL_FACING);

        makeAncientCrop(ModBlocks.ANCIENT_CROP.get(), "ancient_crop");
        tallFlower(ModBlocks.FORSYTHIA);
        tintedCrossBlock(ModBlocks.FOUR_LEAF_CLOVER);
        tintedCrossBlock(ModBlocks.TIGRIS_FLOWER);
        flowerPotBlock(ModBlocks.POTTED_TIGRIS, ModBlocks.TIGRIS_FLOWER);

        createOreBerries(ModBlocks.COPPER_ORE_BERRY);
        createOreBerries(ModBlocks.IRON_ORE_BERRY);
        createOreBerries(ModBlocks.GOLD_ORE_BERRY);
        createOreBerries(ModBlocks.EXPERIENCE_ORE_BERRY);
        createOreBerries(ModBlocks.MANGANESE_ORE_BERRY);

        ModMachines.MACHINES.getMachines().forEach(holder -> {
            if(!holder.hasCustomRender()) {
                if (holder.getBlock() instanceof MachineFourWayBlock<?>)
                    machineBlock(holder.blockHolder(), holder.hasUniqueTexture());
                else if (holder.getBlock() instanceof MachineSixWayBlock<?>)
                    machineBlockSixWay(holder.blockHolder(), holder.hasUniqueTexture());
            }
        });
        ModBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel)
                .forEach(family -> simpleBlockItem(family.getBaseBlock(), cubeAll(family.getBaseBlock())));

    }

    private void machineBlock(DeferredHolder<Block, ? extends Block> holder, boolean unique) {
        String name = holder.getId().getPath();
        String top = unique ? "block/"+name+"_top" : "block/machine_top";
        String bottom = unique ? "block/"+name+"_bottom" : "block/machine_bottom";
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
    private void machineBlockSixWay(DeferredHolder<Block, ? extends Block> holder, boolean unique) {
        String name = holder.getId().getPath();
        String top = unique ? "block/"+name+"_top" : "block/machine_top";
        String bottom = unique ? "block/"+name+"_bottom" : "block/machine_bottom";
        String siding = unique ? "block/"+name+"_side" : "block/machine_side";
        directionBlock(holder.get(), (state, builder) ->
            builder.modelFile(models().cube(name,
                    modLoc(bottom),
                    modLoc(top),
                    modLoc("block/"+name+"_front"),
                    modLoc(siding),
                    modLoc(siding),
                    modLoc(siding))
                .texture("particle", modLoc(siding))), BlockStateProperties.FACING);
        simpleBlockItem(holder.get(), models().getExistingFile(modLoc("block/"+name)));
    }
    private void columnBlockWithItem(DeferredHolder<Block, ? extends Block> holder) {
        columnBlockWithItem(holder, holder.getId().getPath());
    }
    private void columnBlockWithItem(DeferredHolder<Block, ? extends Block> holder, String name) {
        ResourceLocation side = modLoc("block/"+name+"_side");
        ResourceLocation end = modLoc("block/"+name+"_end");
        ModelFile model = models().cubeColumnHorizontal(name, side, end);
        simpleBlock(holder.get(), model);
        simpleBlockItem(holder.get(), model);
    }
    private void deepStorageUnit(DeferredHolder<Block, ? extends Block> holder) {
        bottomTop(holder, holder.getId().getPath());
    }
    private void bottomTop(DeferredHolder<Block, ? extends Block> holder, String name) {
        ModelFile model = models().cubeBottomTop(name,
            modLoc("block/"+name+"_side"),
            modLoc("block/"+name+"_bottom"),
            modLoc("block/"+name+"_top"));
        simpleBlock(holder.get(), model);
        //simpleBlockItem(holder.get(), model);
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

    private <T extends FlowerPotBlock, F extends FlowerBlock> void flowerPotBlock(DeferredBlock<T> block, DeferredBlock<F> flower) {
        ModelFile model = models()
                .withExistingParent(block.getId().getPath(), mcLoc("block/flower_pot_cross"))
                .texture("plant","block/" + flower.getId().getPath())
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

    private void createDrums(DeferredBlock<?> deferred, Block particle) {
        VariantBlockStateBuilder cdVariant = getVariantBuilder(deferred.get());
        cdVariant.forAllStates(state -> {
           String path = deferred.getId().getPath();
           ResourceLocation particleRL = BuiltInRegistries.BLOCK.getKey(particle);
           ModelFile model = models().withExistingParent(path, new ResourceLocation(MODID, "block/base_drum"))
                   .texture("0", "block/"+path).texture("particle", particleRL.getNamespace()+":block/"+particleRL.getPath());
           return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    private void createSpecialStairs(BlockFamily family, String renderType) {
        family.getVariants().values().forEach(block -> {
            if (block instanceof StairBlock stair) stairsBlockWithRenderType(stair, blockTexture(family.getBaseBlock()), renderType);
        });
    }
    private void createSpecialSlab(BlockFamily family, String renderType) {
        family.getVariants().values().forEach(block -> {
            ResourceLocation baseKey = BuiltInRegistries.BLOCK.getKey(family.getBaseBlock());
            ResourceLocation stairKey = BuiltInRegistries.BLOCK.getKey(block);
            ResourceLocation texture = blockTexture(family.getBaseBlock());
            if (block instanceof SlabBlock slab) {
                getVariantBuilder(slab)
                        .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(models().slab(stairKey.getPath(), texture, texture, texture).renderType(renderType)))
                        .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(models().slabTop(stairKey.getPath() + "_top", texture, texture, texture).renderType(renderType)))
                        .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(models().getExistingFile(baseKey)));
            }
        });
    }
    private void createSpecialWall(BlockFamily family, String renderType) {
        family.getVariants().values().forEach(block -> {
            if (block instanceof WallBlock wall) {
                ResourceLocation wallRL = BuiltInRegistries.BLOCK.getKey(wall);
                ResourceLocation texture = blockTexture(family.getBaseBlock());
                wallBlockWithRenderType(wall, texture, renderType);
                ModelFile inventoryModel = models().wallInventory(wallRL.getPath()+"_inventory", texture).renderType(renderType);
                simpleBlockItem(wall, inventoryModel);
            }
        });
    }

    private void createStateForModel(DeferredBlock<?> holder) {
        String path = holder.getId().getPath();
        simpleBlock(holder.get(), new ModelFile.ExistingModelFile(modLoc("block/"+path), models().existingFileHelper));
    }
    
    private void createStateForModel(DeferredBlock<?> holder, Block model) {
        ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(model);
        String namespace = rl.getNamespace();
        String path = rl.getPath();
        simpleBlock(holder.get(), new ModelFile.ExistingModelFile(new ResourceLocation(namespace, "block/"+path), models().existingFileHelper));
    }
    private void createStateForModel(DeferredBlock<?> holder, ResourceLocation rl) {
        String namespace = rl.getNamespace();
        String path = rl.getPath();
        simpleBlock(holder.get(), new ModelFile.ExistingModelFile(new ResourceLocation(namespace, "block/"+path), models().existingFileHelper));
    }

    private void createStateForModelWithProperty(DeferredBlock<?> holder, Property<Direction> property) {
        String path = holder.getId().getPath();
        directionBlock(holder.get(), (state, builder) ->
                builder.modelFile(new ModelFile.ExistingModelFile(modLoc("block/"+path), models().existingFileHelper)), property);
    }

    private void simpleBlockWithItem(Block block) {
        ModelFile model = cubeAll(block);
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void simpleBlockWithItemWithRenderType(Block block, String renderType) {
        ModelFile model = models().cubeAll(BuiltInRegistries.BLOCK.getKey(block).getPath(), blockTexture(block)).renderType(renderType);
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

}
