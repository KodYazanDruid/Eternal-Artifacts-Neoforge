package com.sonamorningstar.eternalartifacts.event.client;

import com.sonamorningstar.eternalartifacts.capabilities.item.CharmStorage;
import com.sonamorningstar.eternalartifacts.client.gui.TabHandler;
import com.sonamorningstar.eternalartifacts.client.gui.screen.CharmsScreen;
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
        slotTextures.put(0 , createSlotSprite(CharmStorage.CharmType.HEAD));
        slotTextures.put(1 , createSlotSprite(CharmStorage.CharmType.NECKLACE));
        slotTextures.put(2 , createSlotSprite(CharmStorage.CharmType.RING));
        slotTextures.put(3 , createSlotSprite(CharmStorage.CharmType.RING));
        slotTextures.put(4 , createSlotSprite(CharmStorage.CharmType.BELT));
        slotTextures.put(5 , createSlotSprite(CharmStorage.CharmType.FEET));
        slotTextures.put(6 , createSlotSprite(CharmStorage.CharmType.HAND));
        slotTextures.put(7 , createSlotSprite(CharmStorage.CharmType.HAND));
        slotTextures.put(8 , createSlotSprite(CharmStorage.CharmType.BRACELET));
        slotTextures.put(9 , createSlotSprite(CharmStorage.CharmType.BACK));
        slotTextures.put(10, createSlotSprite(CharmStorage.CharmType.CHARM));
        slotTextures.put(11, createSlotSprite(CharmStorage.CharmType.CHARM));
    }
    private static ResourceLocation createSlotSprite(CharmStorage.CharmType type) {
        return new ResourceLocation(MODID, "slots/empty_"+type.getLowerCaseName()+"_slot");
    }
}
