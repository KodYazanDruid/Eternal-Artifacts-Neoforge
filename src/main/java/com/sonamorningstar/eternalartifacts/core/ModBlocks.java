package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.*;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    /*public static final DeferredBlock<Block> LUTFI = registerWithItem("lutfi",
                ()-> new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(0.5f).mapColor(MapColor.COLOR_BROWN)), new Item.Properties());
*/
    public static final DeferredBlock<AnvilinatorBlock> ANVILINATOR = registerWithItem("anvilinator",
                ()-> new AnvilinatorBlock(Blocks.ANVIL.properties()), new Item.Properties());

    public static final DeferredBlock<BioFurnaceBlock> BIOFURNACE = registerWithItem("biofurnace",
                ()-> new BioFurnaceBlock(Blocks.ANVIL.properties()), new Item.Properties());

    public static final DeferredBlock<ResonatorBlock> RESONATOR = registerWithItem("resonator",
                ()-> new ResonatorBlock(Blocks.DEEPSLATE.properties(), 128), new Item.Properties());

    public static final DeferredBlock<GardeningPotBlock> GARDENING_POT = registerNoItem("gardening_pot",
                ()-> new GardeningPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA).randomTicks()));

    public static final DeferredBlock<AncientCropBlock> ANCIENT_CROP = registerNoItem("ancient_crop",
                ()-> new AncientCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));

    private static <T extends Block> DeferredBlock<T> registerNoItem(String name, Supplier<T> supplier) { return BLOCKS.register(name, supplier); }
    private static <T extends Block> DeferredBlock<T> registerWithItem(String name, Supplier<T> supplier, Item.Properties props){
        DeferredBlock<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), props));
        return block;
    }

}

