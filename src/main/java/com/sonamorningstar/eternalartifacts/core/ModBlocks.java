package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.client.renderer.BEWLRProps;
import com.sonamorningstar.eternalartifacts.content.block.*;
import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import com.sonamorningstar.eternalartifacts.content.item.block.DrumBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.TigrisFlowerItem;
import com.sonamorningstar.eternalartifacts.content.item.block.base.BewlrMachineItem;
import com.sonamorningstar.eternalartifacts.content.item.block.base.MachineBlockItem;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static net.minecraft.world.level.block.Blocks.CAULDRON;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredBlock<Block> MACHINE_BLOCK = registerWithItem("machine_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(MapColor.STONE)));
    public static final DeferredBlock<RotatedPillarBlock> ROSY_FROGLIGHT = registerWithItem("rosy_froglight",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.3F).lightLevel(p_220869_ -> 15).sound(SoundType.FROGLIGHT)));
    public static final DeferredBlock<Block> GRAVEL_COAL_ORE = registerGravelOres("gravel_coal_ore", UniformInt.of(0, 2));
    public static final DeferredBlock<Block> GRAVEL_COPPER_ORE = registerGravelOres("gravel_copper_ore");
    public static final DeferredBlock<Block> GRAVEL_IRON_ORE = registerGravelOres("gravel_iron_ore");
    public static final DeferredBlock<Block> GRAVEL_GOLD_ORE = registerGravelOres("gravel_gold_ore");
    public static final DeferredBlock<Block> CHLOROPHYTE_DEBRIS = registerWithItem("chlorophyte_debris",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.3F).sound(SoundType.MOSS)));
    public static final DeferredBlock<Block> MANGANESE_ORE = registerWithItem("manganese_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
    public static final DeferredBlock<Block> DEEPSLATE_MANGANESE_ORE = registerWithItem("deepslate_manganese_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final DeferredBlock<Block> RAW_MANGANESE_BLOCK = registerWithItem("raw_manganese_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)));
    public static final DeferredBlock<Block> CHARCOAL_BLOCK = registerWithItem("charcoal_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK)));
    public static final DeferredBlock<Block> MARIN_ORE = registerWithItem("marin_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_GOLD_ORE).mapColor(DyeColor.ORANGE)));
    public static final DeferredBlock<Block> RAW_MARIN_BLOCK = registerWithItem("raw_marin_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_GOLD_BLOCK).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> MARIN_BLOCK = registerWithItem("marin_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<Block> STEEL_BLOCK = registerWithItem("steel_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(7.0f, 8.0f).mapColor(MapColor.COLOR_GRAY)));
    public static final DeferredBlock<Block> TEMPERED_GLASS = registerWithItem("tempered_glass",
            () -> new TemperedGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(25.0f, 3600000.0F).mapColor(MapColor.COLOR_PURPLE).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DEMON_BLOCK = registerWithItem("demon_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK).mapColor(MapColor.COLOR_RED)));

    public static final DeferredBlock<Block> SANDY_TILED_STONE_BRICKS = registerWithItem("sandy_tiled_stone_bricks",
            ()-> new Block(Blocks.STONE_BRICKS.properties()));
    public static final DeferredBlock<Block> SANDY_STONE_BRICKS = registerWithItem("sandy_stone_bricks",
            () -> new Block(Blocks.STONE_BRICKS.properties()));
    public static final DeferredBlock<Block> SANDY_PRISMARINE = registerWithItem("sandy_prismarine",
            () -> new Block(Blocks.PRISMARINE.properties()));
    public static final DeferredBlock<Block> VERY_SANDY_PRISMARINE = registerWithItem("very_sandy_prismarine",
            () -> new Block(Blocks.PRISMARINE.properties()));
    public static final DeferredBlock<Block> SANDY_DARK_PRISMARINE = registerWithItem("sandy_dark_prismarine",
            () -> new Block(Blocks.DARK_PRISMARINE.properties()));
    public static final DeferredBlock<Block> VERY_SANDY_DARK_PRISMARINE = registerWithItem("very_sandy_dark_prismarine",
            () -> new Block(Blocks.DARK_PRISMARINE.properties()));
    public static final DeferredBlock<Block> PAVED_PRISMARINE_BRICKS = registerWithItem("paved_prismarine_bricks",
            () -> new Block(Blocks.PRISMARINE_BRICKS.properties()));
    public static final DeferredBlock<Block> SANDY_PAVED_PRISMARINE_BRICKS = registerWithItem("sandy_paved_prismarine_bricks",
            () -> new Block(Blocks.PRISMARINE_BRICKS.properties()));
    public static final DeferredBlock<Block> SANDY_PRISMARINE_BRICKS = registerWithItem("sandy_prismarine_bricks",
            () -> new Block(Blocks.PRISMARINE_BRICKS.properties()));
    public static final DeferredBlock<Block> LAYERED_PRISMARINE = registerWithItem("layered_prismarine",
            () -> new Block(Blocks.PRISMARINE.properties()));
    public static final DeferredBlock<RotatedPillarBlock> CITRUS_LOG = registerWithItem("citrus_log",
            ()-> new RotatedPillarBlock(Blocks.JUNGLE_LOG.properties()));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CITRUS_LOG = registerWithItem("stripped_citrus_log",
            ()-> new RotatedPillarBlock(Blocks.JUNGLE_LOG.properties()));
    public static final DeferredBlock<RotatedPillarBlock> CITRUS_WOOD = registerWithItem("citrus_wood",
            ()-> new RotatedPillarBlock(Blocks.JUNGLE_WOOD.properties()));
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_CITRUS_WOOD = registerWithItem("stripped_citrus_wood",
            ()-> new RotatedPillarBlock(Blocks.JUNGLE_WOOD.properties()));
    public static final DeferredBlock<Block> CITRUS_PLANKS = registerWithItem("citrus_planks",
            ()-> new Block(Blocks.JUNGLE_PLANKS.properties()));
    public static final DeferredBlock<Block> SNOW_BRICKS = registerWithItem("snow_bricks",
            ()-> new Block(ModProperties.Blocks.SNOW_BRICKS));
    public static final DeferredBlock<Block> SNOW_BRICK_SLAB = registerWithItem("snow_brick_slab",
            ()-> new SlabBlock(ModProperties.Blocks.SNOW_BRICKS));
    public static final DeferredBlock<Block> SNOW_BRICK_STAIRS = registerWithItem("snow_brick_stairs",
            ()-> new StairBlock(()-> SNOW_BRICKS.get().defaultBlockState(), ModProperties.Blocks.SNOW_BRICKS));
    public static final DeferredBlock<Block> SNOW_BRICK_WALL = registerWithItem("snow_brick_wall",
            ()-> new WallBlock(ModProperties.Blocks.SNOW_BRICKS));
    public static final DeferredBlock<Block> ICE_BRICKS = registerWithItem("ice_bricks",
            ()-> new IceBricksBlock(ModProperties.Blocks.ICE_BRICKS));
    public static final DeferredBlock<Block> ICE_BRICK_SLAB = registerWithItem("ice_brick_slab",
            ()-> new IceBrickSlab(ModProperties.Blocks.ICE_BRICKS));
    public static final DeferredBlock<Block> ICE_BRICK_STAIRS = registerWithItem("ice_brick_stairs",
            ()-> new IceBrickStairs(()-> ICE_BRICKS.get().defaultBlockState(), ModProperties.Blocks.ICE_BRICKS));
    public static final DeferredBlock<Block> ICE_BRICK_WALL = registerWithItem("ice_brick_wall",
            ()-> new IceBrickWall(ModProperties.Blocks.ICE_BRICKS));

    public static final DeferredBlock<Block> ASPHALT_BLOCK = registerWithItem("asphalt_block",
            ()-> new AsphaltBlock(Blocks.DEEPSLATE.properties()));
    public static final DeferredBlock<Block> OBSIDIAN_BRICKS = registerWithItem("obsidian_bricks",
            ()-> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).pushReaction(PushReaction.BLOCK)));
    public static final DeferredBlock<Block> OBSIDIAN_BRICK_SLAB = registerWithItem("obsidian_brick_slab",
            ()-> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).pushReaction(PushReaction.BLOCK)));
    public static final DeferredBlock<Block> OBSIDIAN_BRICK_STAIRS = registerWithItem("obsidian_brick_stairs",
            ()-> new StairBlock(()-> OBSIDIAN_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).pushReaction(PushReaction.BLOCK)));
    public static final DeferredBlock<Block> OBSIDIAN_BRICK_WALL = registerWithItem("obsidian_brick_wall",
            ()-> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).pushReaction(PushReaction.BLOCK)));

    public static final DeferredBlock<AnvilinatorBlock> ANVILINATOR = registerMachineWithItem("anvilinator",
            ()-> new AnvilinatorBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<BookDuplicatorBlock> BOOK_DUPLICATOR = registerMachineWithItem("book_duplicator",
            ()-> new BookDuplicatorBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<BatteryBoxBlock> BATTERY_BOX = registerWithItem("battery_box",
            ()-> new BatteryBoxBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<FluidCombustionDynamoBlock> FLUID_COMBUSTION_DYNAMO = registerMachineWithBewlr("fluid_combustion_dynamo",
            () -> new FluidCombustionDynamoBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<NousTankBlock> NOUS_TANK = registerWithBewlr("nous_tank",
            () -> new NousTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).forceSolidOn()));
    public static final DeferredBlock<TrashCanBlock> TRASH_CAN = registerWithItem("trash_can",
        () -> new TrashCanBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)));

    public static final DeferredBlock<CableBlock> COPPER_CABLE = registerWithItem("copper_cable",
            ()-> new UncoveredCableBlock(ModProperties.Blocks.CABLE));
    public static final DeferredBlock<CableBlock> COVERED_COPPER_CABLE = registerWithItem("covered_copper_cable",
            ()-> new CableBlock(ModProperties.Blocks.CABLE));

    public static final DeferredBlock<BioFurnaceBlock> BIOFURNACE = registerMachineWithItem("biofurnace",
            ()-> new BioFurnaceBlock(Blocks.ANVIL.properties()));
    public static final DeferredBlock<ResonatorBlock> RESONATOR = registerWithItem("resonator",
            ()-> new ResonatorBlock(Blocks.DEEPSLATE.properties().forceSolidOn(), 128));
    public static final DeferredBlock<EnergyDockBlock> ENERGY_DOCK = registerWithBewlr("energy_dock",
            ()-> new EnergyDockBlock(Blocks.DEEPSLATE.properties().forceSolidOn()));
    public static final DeferredBlock<Block> SHOCK_ABSORBER = registerMachineWithItem("shock_absorber", ShockAbsorberBlock::new);
    public static final DeferredBlock<Block> MACHINE_WORKBENCH = registerMachineWithItem("machine_workbench", MachineWorkbench::new);
    
    public static final DeferredBlock<DrumBlock> COPPER_DRUM = registerDrum("copper_drum", Blocks.COPPER_BLOCK.properties(), 32000);
    public static final DeferredBlock<DrumBlock> IRON_DRUM = registerDrum("iron_drum", Blocks.IRON_BLOCK.properties(), 64000);
    public static final DeferredBlock<DrumBlock> GOLD_DRUM = registerDrum("gold_drum", Blocks.GOLD_BLOCK.properties(), 128000);
    //public static final DeferredBlock<DrumBlock> DEMONIC_DRUM = registerDrum("demonic_drum", Blocks.GOLD_BLOCK.properties(), 256000);
    public static final DeferredBlock<DrumBlock> STEEL_DRUM = registerDrum("steel_drum", BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(7.0f, 8.0f).mapColor(MapColor.COLOR_GRAY), 512000);
    public static final DeferredBlock<DrumBlock> DIAMOND_DRUM = registerDrum("diamond_drum", Blocks.DIAMOND_BLOCK.properties(), 1024000);
    public static final DeferredBlock<DrumBlock> NETHERITE_DRUM = registerDrum("netherite_drum", Blocks.NETHERITE_BLOCK.properties(), 2048000, new Item.Properties().fireResistant());

    public static final DeferredBlock<PinkSlimeBlock> PINK_SLIME_BLOCK = registerWithItem("pink_slime_block",
            ()-> new PinkSlimeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK)));
    public static final DeferredBlock<Block> SUGAR_CHARCOAL_BLOCK = registerWithItem("sugar_charcoal_block",
            ()-> new Block(Blocks.COAL_BLOCK.properties()));

    public static final DeferredBlock<Block> PLASTIC_CAULDRON = registerNoItem("plastic_cauldron", PlasticCauldronBlock::new);
    public static final DeferredBlock<Block> BLUE_PLASTIC_CAULDRON = registerNoItem("blue_plastic_cauldron",
            ()-> new BluePlasticCauldronBlock(BlockBehaviour.Properties.ofFullCopy(CAULDRON)));

    public static final DeferredBlock<GardeningPotBlock> GARDENING_POT = registerNoItem("gardening_pot", GardeningPotBlock::new);
    public static final DeferredBlock<JarBlock> JAR = registerNoItem("jar",
            ()-> new JarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).forceSolidOn()));
    public static final DeferredBlock<FancyChestBlock> FANCY_CHEST = registerNoItem("fancy_chest",
            ()-> new FancyChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)));
    public static final DeferredBlock<TesseractBlock> TESSERACT = registerNoItem("tesseract",
            ()-> new TesseractBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN).noOcclusion()));
    
    public static final DeferredBlock<AncientCropBlock> ANCIENT_CROP = registerNoItem("ancient_crop",
            ()-> new AncientCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));
    public static final DeferredBlock<TallFlowerBlock> FORSYTHIA = registerDoublePlant("forsythia",
            ()-> new TallFlowerBlock(ModProperties.Blocks.FLOWER));
    public static final DeferredBlock<FlowerBlock> FOUR_LEAF_CLOVER = registerWithItem("four_leaf_clover",
            ()-> new FlowerBlock(()-> MobEffects.LUCK, 15, ModProperties.Blocks.FLOWER));
    public static final DeferredBlock<PunjiBlock> PUNJI_STICKS = registerWithItem("punji_sticks",
            ()-> new PunjiBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .sound(SoundType.BAMBOO)
                    .noOcclusion()
                    .strength(0.3F)
            ));
    public static final DeferredBlock<FlowerBlock> TIGRIS_FLOWER = registerWithItem("tigris_flower", TigrisFlowerBlock::new, TigrisFlowerItem.class);
    public static final DeferredBlock<FlowerPotBlock> POTTED_TIGRIS = registerNoItem("potted_tigris", ()-> flowerPot(TIGRIS_FLOWER));

    public static final DeferredBlock<OreBerryBlock> COPPER_ORE_BERRY = registerOreBerryBlock("copper");
    public static final DeferredBlock<OreBerryBlock> IRON_ORE_BERRY = registerOreBerryBlock("iron");
    public static final DeferredBlock<OreBerryBlock> GOLD_ORE_BERRY = registerOreBerryBlock("gold");
    public static final DeferredBlock<OreBerryBlock> EXPERIENCE_ORE_BERRY = registerOreBerryBlock("experience");
    public static final DeferredBlock<OreBerryBlock> MANGANESE_ORE_BERRY = registerOreBerryBlock("manganese");
    
    public static final DeferredBlock<SkullBlock> DROWNED_HEAD = registerNoItem("drowned_head",
            ()-> new ModSkullBlock(ModSkullType.DROWNED, BlockBehaviour.Properties.ofFullCopy(Blocks.ZOMBIE_HEAD)));
    public static final DeferredBlock<WallSkullBlock> DROWNED_WALL_HEAD = registerNoItem("drowned_wall_head",
            ()-> new ModWallSkullBlock(ModSkullType.DROWNED, BlockBehaviour.Properties.ofFullCopy(Blocks.ZOMBIE_WALL_HEAD).lootFrom(DROWNED_HEAD)));
    public static final DeferredBlock<SkullBlock> HUSK_HEAD = registerNoItem("husk_head",
            ()-> new ModSkullBlock(ModSkullType.HUSK, BlockBehaviour.Properties.ofFullCopy(Blocks.ZOMBIE_HEAD)));
    public static final DeferredBlock<WallSkullBlock> HUSK_WALL_HEAD = registerNoItem("husk_wall_head",
            ()-> new ModWallSkullBlock(ModSkullType.HUSK, BlockBehaviour.Properties.ofFullCopy(Blocks.ZOMBIE_WALL_HEAD).lootFrom(HUSK_HEAD)));
    public static final DeferredBlock<SkullBlock> STRAY_SKULL = registerNoItem("stray_skull",
            ()-> new ModSkullBlock(ModSkullType.STRAY, BlockBehaviour.Properties.ofFullCopy(Blocks.SKELETON_SKULL)));
    public static final DeferredBlock<WallSkullBlock> STRAY_WALL_SKULL = registerNoItem("stray_wall_skull",
            ()-> new ModWallSkullBlock(ModSkullType.STRAY, BlockBehaviour.Properties.ofFullCopy(Blocks.SKELETON_WALL_SKULL).lootFrom(STRAY_SKULL)));
    public static final DeferredBlock<SkullBlock> BLAZE_HEAD = registerNoItem("blaze_head",
        ()-> new ModSkullBlock(ModSkullType.BLAZE, BlockBehaviour.Properties.ofFullCopy(Blocks.WITHER_SKELETON_SKULL)));
    public static final DeferredBlock<WallSkullBlock> BLAZE_WALL_HEAD = registerNoItem("blaze_wall_head",
        ()-> new ModWallSkullBlock(ModSkullType.BLAZE, BlockBehaviour.Properties.ofFullCopy(Blocks.WITHER_SKELETON_WALL_SKULL).lootFrom(BLAZE_HEAD)));

    //region Registry functions.
    private static <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<T> supplier) { return BLOCKS.register(name, supplier); }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier){
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
    private static <T extends Block> DeferredBlock<T> registerMachineWithItem(String name, Supplier<T> supplier){
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new MachineBlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static <T extends Block, I extends BlockItem> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Class<I> clazz) {
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> {
            try {
                Constructor<I> cons = clazz.getDeclaredConstructor(Block.class, Item.Properties.class);
                return cons.newInstance(block.get(), new Item.Properties());
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
        return block;
    }
    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.Properties props){
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), props));
        return block;
    }
    private static <T extends Block, E extends BlockItem> DeferredBlock<T> registerWithItem(String name, Supplier<T> blockSup, Class<E> itemClass, Item.Properties itemProps) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSup);
        ModItems.ITEMS.register(name, ()-> {
            try {
                return itemClass.getDeclaredConstructor(Block.class, Item.Properties.class).newInstance(block.get(), itemProps);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerDoublePlant(String name, Supplier<T> supplier) {
        DeferredBlock<T> doublePlant = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new DoubleHighBlockItem(doublePlant.get(), new Item.Properties()));
        return doublePlant;
    }

    private static DeferredBlock<OreBerryBlock> registerOreBerryBlock(String material) {
        return registerWithItem(material+"_oreberry", ()-> new OreBerryBlock(ModProperties.Blocks.ORE_BERRY, material));
    }
    private static DeferredBlock<Block> registerGravelOres(String name) {
        return registerGravelOres(name, ConstantInt.of(0));
    }
    private static DeferredBlock<Block> registerGravelOres(String name, IntProvider exp) {
        return registerWithItem(name, () -> new FallingDropExperienceBlock(exp, BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)));
    }

    private static DeferredBlock<FluidCombustionDynamoBlock> registerDynamo(String name) {
        return registerWithBewlr(name, () -> new FluidCombustionDynamoBlock(MACHINE_BLOCK.get().properties()));
    }

    private static DeferredBlock<DrumBlock> registerDrum(String name, BlockBehaviour.Properties material, int cap, Item.Properties... itemProps) {
        return registerWithItem(name, ()-> new DrumBlock(material, cap), DrumBlockItem.class, itemProps.length > 0 ? itemProps[0] : new Item.Properties());
    }

    private static <T extends Block> DeferredBlock<T> registerWithBewlr(String name, Supplier<T> sup) {
        DeferredBlock<T> block = BLOCKS.register(name, sup);
        ModItems.ITEMS.register(name, ()-> new BlockItem(block.get(), new Item.Properties()) {
            @Override
            public void initializeClient(Consumer<IClientItemExtensions> consumer) {consumer.accept(BEWLRProps.INSTANCE);}
        });
        return block;
    }
    private static <T extends Block> DeferredBlock<T> registerMachineWithBewlr(String name, Supplier<T> sup) {
        DeferredBlock<T> block = BLOCKS.register(name, sup);
        ModItems.ITEMS.register(name, ()-> new BewlrMachineItem(block.get(), new Item.Properties()));
        return block;
    }

    private static <T extends Block> FlowerPotBlock flowerPot(DeferredBlock<T> block) {
        return new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT), block, BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));
    }
    //endregion

}

