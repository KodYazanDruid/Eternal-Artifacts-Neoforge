package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.*;
import com.sonamorningstar.eternalartifacts.content.fluid.PinkSlimeLiquidBlock;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    /*public static final DeferredBlock<Block> LUTFI = registerWithItem("lutfi",
                ()-> new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(0.5f).mapColor(MapColor.COLOR_BROWN)), new Item.Properties());
*/
    public static final DeferredBlock<Block> MACHINE_BLOCK = registerWithItem("machine_block",
            ()-> new Block(Blocks.IRON_BLOCK.properties().mapColor(MapColor.STONE)), new Item.Properties());
    public static final DeferredBlock<RotatedPillarBlock> ROSY_FROGLIGHT = registerWithItem("rosy_froglight",
            ()-> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.3F).lightLevel(p_220869_ -> 15).sound(SoundType.FROGLIGHT)),
            new Item.Properties());
    public static final DeferredBlock<Block> GRAVEL_COAL_ORE = registerWithItem("gravel_coal_ore",
            () -> new FallingDropExperienceBlock(UniformInt.of(0, 2), Blocks.GRAVEL.properties()), new Item.Properties());
    public static final DeferredBlock<Block> GRAVEL_COPPER_ORE = registerWithItem("gravel_copper_ore",
            () -> new FallingDropExperienceBlock(ConstantInt.of(0), Blocks.GRAVEL.properties()), new Item.Properties());
    public static final DeferredBlock<Block> GRAVEL_IRON_ORE = registerWithItem("gravel_iron_ore",
            () -> new FallingDropExperienceBlock(ConstantInt.of(0), Blocks.GRAVEL.properties()), new Item.Properties());
    public static final DeferredBlock<Block> GRAVEL_GOLD_ORE = registerWithItem("gravel_gold_ore",
            () -> new FallingDropExperienceBlock(ConstantInt.of(0), Blocks.GRAVEL.properties()), new Item.Properties());

    public static final DeferredBlock<LiquidBlock> NOUS_BLOCK = registerNoItem("nous_block",
            ()-> new LiquidBlock(ModFluids.NOUS_SOURCE, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final DeferredBlock<LiquidBlock> LIQUID_MEAT_BLOCK = registerNoItem("liquid_meat_block",
            ()-> new LiquidBlock(ModFluids.LIQUID_MEAT_SOURCE, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_BROWN)));
    public static final DeferredBlock<LiquidBlock> PINK_SLIME_FLUID_BLOCK = registerNoItem("pink_slime_fluid_block",
            ()-> new PinkSlimeLiquidBlock(ModFluids.PINK_SLIME_SOURCE, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_PINK)));

    public static final DeferredBlock<AnvilinatorBlock> ANVILINATOR = registerWithItem("anvilinator",
            ()-> new AnvilinatorBlock(Blocks.IRON_BLOCK.properties()), new Item.Properties());
    public static final DeferredBlock<BookDuplicatorBlock> BOOK_DUPLICATOR = registerWithItem("book_duplicator",
            ()-> new BookDuplicatorBlock(Blocks.IRON_BLOCK.properties()), new Item.Properties());

    public static final DeferredBlock<BioFurnaceBlock> BIOFURNACE = registerWithItem("biofurnace",
            ()-> new BioFurnaceBlock(Blocks.ANVIL.properties()), new Item.Properties());

    public static final DeferredBlock<ResonatorBlock> RESONATOR = registerWithItem("resonator",
            ()-> new ResonatorBlock(Blocks.DEEPSLATE.properties(), 128), new Item.Properties());

    public static final DeferredBlock<PinkSlimeBlock> PINK_SLIME_BLOCK = registerWithItem("pink_slime_block",
            ()-> new PinkSlimeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK)), new Item.Properties());
    public static final DeferredBlock<Block> SUGAR_CHARCOAL_BLOCK = registerWithItem("sugar_charcoal_block",
            ()-> new Block(Blocks.COAL_BLOCK.properties()), new Item.Properties());

    public static final DeferredBlock<GardeningPotBlock> GARDENING_POT = registerNoItem("gardening_pot",
            ()-> new GardeningPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA).randomTicks()));

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
            ), new Item.Properties());

    private static <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<T> supplier) { return BLOCKS.register(name, supplier); }
    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.Properties props){
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), props));
        return block;
    }

}

