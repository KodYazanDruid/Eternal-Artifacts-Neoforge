package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.block.GardeningPotBlock;
import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(MODID+"_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(ModItems.ORANGE.get()::getDefaultInstance)
            .displayItems((parameters, output) -> {
                for(DeferredHolder<Item, ? extends Item> item : ModItems.ITEMS.getEntries()) {
                    if(item.get() instanceof RetexturedBlockItem pot) pot.fillItemCategory(output);
                    else output.accept(item.get());
                }
                for(DeferredHolder<Block, ? extends Block> block : ModBlocks.BLOCKS.getEntries()) {
                    if(!(block.get() instanceof GardeningPotBlock) && !(block.get() instanceof LiquidBlock)) {
                        output.accept(block.get());
                    }
                }
            }).build());

}
