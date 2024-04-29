package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.*;
import com.sonamorningstar.eternalartifacts.core.ModItems;
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

    private static <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<T> supplier) { return BLOCKS.register(name, supplier); }
    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.Properties props){
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), props));
        return block;
    }

}

