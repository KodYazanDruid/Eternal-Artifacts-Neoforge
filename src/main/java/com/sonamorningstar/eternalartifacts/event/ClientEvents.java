package com.sonamorningstar.eternalartifacts.event;

import com.sonamorningstar.eternalartifacts.client.GardeningPotColor;
import com.sonamorningstar.eternalartifacts.client.gui.screen.AnvilinatorScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.BioFurnaceScreen;
import com.sonamorningstar.eternalartifacts.client.model.ColoredBlockModel;
import com.sonamorningstar.eternalartifacts.client.model.RetexturedModel;
import com.sonamorningstar.eternalartifacts.content.entity.client.DemonEyeModel;
import com.sonamorningstar.eternalartifacts.content.entity.client.DemonEyeRenderer;
import com.sonamorningstar.eternalartifacts.content.entity.client.ModModelLayers;
import com.sonamorningstar.eternalartifacts.content.item.EncumbatorItem;
import com.sonamorningstar.eternalartifacts.content.item.block.GardeningPotBlockItem;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents{

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.ANVILINATOR.get(), AnvilinatorScreen::new);
        event.register(ModMenuTypes.BIOFURNACE.get(), BioFurnaceScreen::new);
    }

    @SubscribeEvent
    public static void registerColorHandlerItem(RegisterColorHandlersEvent.Item event) {
        event.register((pStack, pTintIndex) -> {
            if(pTintIndex == 1) return ((BaseFluidType) ModFluidTypes.NOUS.get()).getTintColor();
            else return -1;
        }, ModItems.NOUS_BUCKET.get());

        event.register(new GardeningPotColor() , ModItems.GARDENING_POT.get());
    }

    @SubscribeEvent
    public static void registerColorHandlerBlock(RegisterColorHandlersEvent.Block event) {
        event.register(new GardeningPotColor(), ModBlocks.GARDENING_POT.get());
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(new ResourceLocation(MODID, "colored_block"), ColoredBlockModel.LOADER);
        event.register(new ResourceLocation(MODID, "retextured"), RetexturedModel.LOADER);
    }

    @SubscribeEvent
    public static void fmlClient(FMLClientSetupEvent event) {
        event.enqueueWork(
        ()->
        ItemProperties.register(ModItems.ENCUMBATOR.get(), new ResourceLocation(MODID, "active"), (s, l, e, sd) -> EncumbatorItem.isStackActive(s) ? 1.0F : 0.0F)
        );

        EntityRenderers.register(ModEntities.DEMON_EYE.get(), DemonEyeRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.DEMON_EYE_LAYER, DemonEyeModel::createBodyLayer);
    }



}