package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.*;
import com.sonamorningstar.eternalartifacts.content.fluid.PinkSlimeLiquidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

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

    public static final DeferredBlock<LiquidBlock> NOUS_BLOCK = registerNoItem("nous_block",
            ()-> new LiquidBlock(ModFluids.NOUS_SOURCE, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final DeferredBlock<LiquidBlock> LIQUID_MEAT_BLOCK = registerNoItem("liquid_meat_block",
            ()-> new LiquidBlock(ModFluids.LIQUID_MEAT_SOURCE, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_BROWN)));
    public static final DeferredBlock<LiquidBlock> PINK_SLIME_FLUID_BLOCK = registerNoItem("pink_slime_fluid_block",
            ()-> new PinkSlimeLiquidBlock(ModFluids.PINK_SLIME_SOURCE, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_PINK)));
    public static final DeferredBlock<LiquidBlock> BLOOD_BLOCK = registerNoItem("blood_block",
            ()-> new LiquidBlock(ModFluids.BLOOD_SOURCE, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_RED)));

    public static final DeferredBlock<AnvilinatorBlock> ANVILINATOR = registerWithItem("anvilinator",
            ()-> new AnvilinatorBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<BookDuplicatorBlock> BOOK_DUPLICATOR = registerWithItem("book_duplicator",
            ()-> new BookDuplicatorBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<MeatPackerBlock> MEAT_PACKER = registerWithItem("meat_packer",
            ()-> new MeatPackerBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<MeatShredderBlock> MEAT_SHREDDER = registerWithItem("meat_shredder",
            ()-> new MeatShredderBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<BatteryBoxBlock> BATTERY_BOX = registerWithItem("battery_box",
            ()-> new BatteryBoxBlock(MACHINE_BLOCK.get().properties()));
    public static final DeferredBlock<MobLiquifierBlock> MOB_LIQUIFIER = registerWithItem("mob_liquifier",
            ()-> new MobLiquifierBlock(MACHINE_BLOCK.get().properties()));

    public static final DeferredBlock<BioFurnaceBlock> BIOFURNACE = registerWithItem("biofurnace",
            ()-> new BioFurnaceBlock(Blocks.ANVIL.properties()));

    public static final DeferredBlock<ResonatorBlock> RESONATOR = registerWithItem("resonator",
            ()-> new ResonatorBlock(Blocks.DEEPSLATE.properties(), 128));

    public static final DeferredBlock<PinkSlimeBlock> PINK_SLIME_BLOCK = registerWithItem("pink_slime_block",
            ()-> new PinkSlimeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK)));
    public static final DeferredBlock<Block> SUGAR_CHARCOAL_BLOCK = registerWithItem("sugar_charcoal_block",
            ()-> new Block(Blocks.COAL_BLOCK.properties()));

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

    private static DeferredBlock<OreBerryBlock> registerOreBerryBlock(String material) {
        return registerWithItem(material+"_oreberry", ()-> new OreBerryBlock(oreBerryProps, material));
    }

    private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {return false;}
    private static Boolean always(BlockState p_50810_, BlockGetter p_50811_, BlockPos p_50812_, EntityType<?> p_50813_) {return true;}
    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {return false;}
    private static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
        return true;
    }



}

