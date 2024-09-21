package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import com.sonamorningstar.eternalartifacts.client.renderer.BEWLRProps;
import com.sonamorningstar.eternalartifacts.content.block.*;
import com.sonamorningstar.eternalartifacts.content.block.CableBlock;
import com.sonamorningstar.eternalartifacts.content.item.block.DrumBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static net.minecraft.world.level.block.Blocks.CAULDRON;

public class ModBlocks {
    static final BlockBehaviour.Properties oreBerryProps = BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .sound(SoundType.COPPER)
            .pushReaction(PushReaction.DESTROY)
            .randomTicks()
            .noOcclusion()
            .isValidSpawn(ModBlocks::never)
            .isRedstoneConductor(ModBlocks::never)
            .isSuffocating(ModBlocks::never)
            .isViewBlocking(ModBlocks::never);

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredBlock<Block> MACHINE_BLOCK = registerWithItem("machine_block",
            () -> new Block(Blocks.IRON_BLOCK.properties().mapColor(MapColor.STONE)));
    public static final DeferredBlock<RotatedPillarBlock> ROSY_FROGLIGHT = registerWithItem("rosy_froglight",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.3F).lightLevel(p_220869_ -> 15).sound(SoundType.FROGLIGHT)));
    public static final DeferredBlock<Block> GRAVEL_COAL_ORE = registerWithItem("gravel_coal_ore",
            () -> new FallingDropExperienceBlock(UniformInt.of(0, 2), Blocks.GRAVEL.properties()));
    public static final DeferredBlock<Block> GRAVEL_COPPER_ORE = registerWithItem("gravel_copper_ore",
            () -> new FallingDropExperienceBlock(ConstantInt.of(0), Blocks.GRAVEL.properties()));
    public static final DeferredBlock<Block> GRAVEL_IRON_ORE = registerWithItem("gravel_iron_ore",
            () -> new FallingDropExperienceBlock(ConstantInt.of(0), Blocks.GRAVEL.properties()));
    public static final DeferredBlock<Block> GRAVEL_GOLD_ORE = registerWithItem("gravel_gold_ore",
            () -> new FallingDropExperienceBlock(ConstantInt.of(0), Blocks.GRAVEL.properties()));
    public static final DeferredBlock<Block> CHLOROPHYTE_DEBRIS = registerWithItem("chlorophyte_debris",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.3F).sound(SoundType.MOSS)));
    public static final DeferredBlock<Block> MANGANESE_ORE = registerWithItem("manganese_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), Blocks.IRON_ORE.properties()));
    public static final DeferredBlock<Block> DEEPSLATE_MANGANESE_ORE = registerWithItem("deepslate_manganese_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), Blocks.DEEPSLATE_IRON_ORE.properties()));
    public static final DeferredBlock<Block> RAW_MANGANESE_BLOCK = registerWithItem("raw_manganese_block",
            () -> new Block(Blocks.RAW_IRON_BLOCK.properties()));
    public static final DeferredBlock<Block> CHARCOAL_BLOCK = registerWithItem("charcoal_block",
            () -> new Block(Blocks.COAL_BLOCK.properties()));
    public static final DeferredBlock<Block> ARDITE_ORE = registerWithItem("ardite_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), Blocks.NETHER_GOLD_ORE.properties().mapColor(DyeColor.ORANGE)));
    public static final DeferredBlock<Block> RAW_ARDITE_BLOCK = registerWithItem("raw_ardite_block",
            () -> new Block(Blocks.RAW_GOLD_BLOCK.properties().mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final DeferredBlock<Block> ARDITE_BLOCK = registerWithItem("ardite_block",
            () -> new Block(Blocks.GOLD_BLOCK.properties().mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<Block> STEEL_BLOCK = registerWithItem("steel_block",
            () -> new Block(Blocks.IRON_BLOCK.properties().strength(7.0f, 8.0f).mapColor(MapColor.COLOR_GRAY)));

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
            ()-> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .requiresCorrectToolForDrops()
                    .strength(0.9F)
                    .explosionResistance(2.0F)
                    .sound(SoundType.SNOW)
            ));
    public static final DeferredBlock<Block> ICE_BRICKS = registerWithItem("ice_bricks",
            ()-> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.ICE)
                    .requiresCorrectToolForDrops()
                    .strength(1.2F)
                    .explosionResistance(2.5F)
                    .sound(SoundType.GLASS)
                    .friction(0.98F)
                    .noOcclusion()
                    .isValidSpawn((state, getter, pos, type) -> type == EntityType.POLAR_BEAR)
                    .isRedstoneConductor(ModBlocks::never)
            ) {
                @Override
                public boolean skipRendering(BlockState state, BlockState adjacent, Direction dir) {
                    return adjacent.is(state.getBlock()) || super.skipRendering(state, adjacent, dir);
                }
            });
    public static final DeferredBlock<Block> ASPHALT_BLOCK = registerWithItem("asphalt_block",
            ()-> new AsphaltBlock(Blocks.DEEPSLATE.properties()));

    public static final DeferredBlock<AnvilinatorBlock> ANVILINATOR = registerWithItem("anvilinator",
            ()-> new AnvilinatorBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<BookDuplicatorBlock> BOOK_DUPLICATOR = registerWithItem("book_duplicator",
            ()-> new BookDuplicatorBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<BatteryBoxBlock> BATTERY_BOX = registerWithItem("battery_box",
            ()-> new BatteryBoxBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<FluidCombustionDynamoBlock> FLUID_COMBUSTION_DYNAMO = registerWithBewlr("fluid_combustion_dynamo",
            () -> new FluidCombustionDynamoBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<NousTankBlock> NOUS_TANK = registerWithBewlr("nous_tank",
            () -> new NousTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).forceSolidOn()));

    public static final DeferredBlock<CableBlock> COPPER_CABLE = registerWithItem("copper_cable",
            ()-> new CableBlock(Blocks.LANTERN.properties().lightLevel(st -> 0).pushReaction(PushReaction.IGNORE)));

    public static final DeferredBlock<BioFurnaceBlock> BIOFURNACE = registerWithItem("biofurnace",
            ()-> new BioFurnaceBlock(Blocks.ANVIL.properties()));
    public static final DeferredBlock<ResonatorBlock> RESONATOR = registerWithItem("resonator",
            ()-> new ResonatorBlock(Blocks.DEEPSLATE.properties().forceSolidOn(), 128));

    public static final DeferredBlock<DrumBlock> COPPER_DRUM = registerDrum("copper_drum", Blocks.COPPER_BLOCK.properties(), 32000);
    public static final DeferredBlock<DrumBlock> IRON_DRUM = registerDrum("iron_drum", Blocks.IRON_BLOCK.properties(), 64000);
    public static final DeferredBlock<DrumBlock> GOLD_DRUM = registerDrum("gold_drum", Blocks.GOLD_BLOCK.properties(), 128000);
    //public static final DeferredBlock<DrumBlock> DEMONIC_DRUM = registerDrum("demonic_drum", Blocks.GOLD_BLOCK.properties(), 256000);
    public static final DeferredBlock<DrumBlock> STEEL_DRUM = registerDrum("steel_drum", Blocks.IRON_BLOCK.properties().strength(7.0f, 8.0f).mapColor(MapColor.COLOR_GRAY), 512000);
    public static final DeferredBlock<DrumBlock> DIAMOND_DRUM = registerDrum("diamond_drum", Blocks.DIAMOND_BLOCK.properties(), 1024000);
    public static final DeferredBlock<DrumBlock> NETHERITE_DRUM = registerDrum("netherite_drum", Blocks.NETHERITE_BLOCK.properties(), 2048000, new Item.Properties().fireResistant());

    public static final DeferredBlock<PinkSlimeBlock> PINK_SLIME_BLOCK = registerWithItem("pink_slime_block",
            ()-> new PinkSlimeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK)));
    public static final DeferredBlock<Block> SUGAR_CHARCOAL_BLOCK = registerWithItem("sugar_charcoal_block",
            ()-> new Block(Blocks.COAL_BLOCK.properties()));

    public static final DeferredBlock<Block> PLASTIC_CAULDRON = registerNoItem("plastic_cauldron",
            ()-> new LayeredCauldronBlock(Biome.Precipitation.NONE, ModCauldronInteraction.PLASTIC, BlockBehaviour.Properties.ofLegacyCopy(CAULDRON)) {
                @Override
                public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
                    return Items.CAULDRON.getDefaultInstance();
                }
            });
    public static final DeferredBlock<Block> BLUE_PLASTIC_CAULDRON = registerNoItem("blue_plastic_cauldron",
            ()-> new BluePlasticCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(CAULDRON)));

    public static final DeferredBlock<GardeningPotBlock> GARDENING_POT = registerNoItem("gardening_pot",
            ()-> new GardeningPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA).noOcclusion().randomTicks()));
    public static final DeferredBlock<JarBlock> JAR = registerNoItem("jar",
            ()-> new JarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).forceSolidOn()));
    public static final DeferredBlock<FancyChestBlock> FANCY_CHEST = registerNoItem("fancy_chest",
            ()-> new FancyChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)));

    public static final DeferredBlock<AncientCropBlock> ANCIENT_CROP = registerNoItem("ancient_crop",
            ()-> new AncientCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));
    public static final DeferredBlock<TallFlowerBlock> FORSYTHIA = registerNoItem("forsythia",
            ()-> new TallFlowerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
            ));
    public static final DeferredBlock<FlowerBlock> FOUR_LEAF_CLOVER = registerWithItem("four_leaf_clover",
            ()-> new FlowerBlock(
                    ()-> MobEffects.LUCK, 15,
                    BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
            ));
    public static final DeferredBlock<PunjiBlock> PUNJI_STICKS = registerWithItem("punji_sticks",
            ()-> new PunjiBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .sound(SoundType.BAMBOO)
                    .noOcclusion()
                    .strength(0.3F)
            ));

    public static final DeferredBlock<OreBerryBlock> COPPER_ORE_BERRY = registerOreBerryBlock("copper");
    public static final DeferredBlock<OreBerryBlock> IRON_ORE_BERRY = registerOreBerryBlock("iron");
    public static final DeferredBlock<OreBerryBlock> GOLD_ORE_BERRY = registerOreBerryBlock("gold");
    public static final DeferredBlock<OreBerryBlock> EXPERIENCE_ORE_BERRY = registerOreBerryBlock("experience");
    public static final DeferredBlock<OreBerryBlock> MANGANESE_ORE_BERRY = registerOreBerryBlock("manganese");

    private static <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<T> supplier) { return BLOCKS.register(name, supplier); }

    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier){
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
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

    private static DeferredBlock<OreBerryBlock> registerOreBerryBlock(String material) {
        return registerWithItem(material+"_oreberry", ()-> new OreBerryBlock(oreBerryProps, material));
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
            public void initializeClient(Consumer<IClientItemExtensions> consumer) {consumer.accept(new BEWLRProps());}
        });
        return block;
    }

    private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {return false;}
    private static Boolean always(BlockState p_50810_, BlockGetter p_50811_, BlockPos p_50812_, EntityType<?> p_50813_) {return true;}
    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {return false;}
    private static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
        return true;
    }



}

