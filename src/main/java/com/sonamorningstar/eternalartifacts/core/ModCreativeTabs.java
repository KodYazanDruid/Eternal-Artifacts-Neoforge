package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
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
                    if(item.get() instanceof RetexturedBlockItem ret) ret.fillItemCategory(output);
                    else output.accept(item.get());
                }
                for (var machineHolder : ModMachines.MACHINES.getMachines()) {
                    output.accept(machineHolder.getItem());
                }
                for (var bucketHolder : ModFluids.FLUIDS.getBucketEntries()) {
                    output.accept(bucketHolder.get());
                }
            }).build());

}
