package com.sonamorningstar.eternalartifacts.event.client;

import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.CharmsScreen;
import com.sonamorningstar.eternalartifacts.content.item.EncumbatorItem;
import com.sonamorningstar.eternalartifacts.content.item.base.IActiveStack;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.event.custom.RegisterTabHoldersEvent;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FMLClientSetup {

    @SubscribeEvent
    public static void fmlClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerItemProperty(ModItems.ENCUMBATOR, "active");
            registerItemProperty(ModItems.BLUEPRINT, "filled");

            TabHandler.registeredTabs = ModRegistries.TAB_TYPE.stream().toList();
            ModLoader.get().postEvent(new RegisterTabHoldersEvent(TabHandler.tabHolders));
            setupCharmSprites();
        });
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HOT_SPRING_WATER.getFluid(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HOT_SPRING_WATER.getFlowingFluid(), RenderType.translucent());
    }

    private static void registerItemProperty(DeferredHolder<Item, ? extends Item> holder, String prefix) {
        ItemProperties.register(holder.get(),
                new ResourceLocation(MODID, prefix), (s, l, e, seed) -> s.getItem() instanceof IActiveStack as && as.isActive(s) ? 1.0F : 0.0F);
    }

    private static void setupCharmSprites() {
        var slotTextures = CharmsScreen.slotTextures;
        slotTextures.put(0 , createSlotSprite("empty_head_slot"));
        slotTextures.put(1 , createSlotSprite("empty_magic_feather_slot"));
        slotTextures.put(2 , createSlotSprite("empty_ring_slot"));
        slotTextures.put(3 , createSlotSprite("empty_ring_slot"));
        slotTextures.put(4 , createSlotSprite("empty_magic_feather_slot"));
        slotTextures.put(5 , createSlotSprite("empty_feet_slot"));
        slotTextures.put(6 , createSlotSprite("empty_magic_feather_slot"));
        slotTextures.put(7 , createSlotSprite("empty_magic_feather_slot"));
        slotTextures.put(8 , createSlotSprite("empty_magic_feather_slot"));
        slotTextures.put(9 , createSlotSprite("empty_back_slot"));
        slotTextures.put(10, createSlotSprite("empty_magic_feather_slot"));
        slotTextures.put(11, createSlotSprite("empty_magic_feather_slot"));
    }
    private static ResourceLocation createSlotSprite(String name) {
        return new ResourceLocation(MODID, "slots/" + name);
    }
}
