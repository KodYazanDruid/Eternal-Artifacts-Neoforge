package com.sonamorningstar.eternalartifacts.event;

import com.sonamorningstar.eternalartifacts.client.RetexturedColor;
import com.sonamorningstar.eternalartifacts.client.gui.screen.AnvilinatorScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.BioFurnaceScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.BookDuplicatorScreen;
import com.sonamorningstar.eternalartifacts.client.model.ColoredBlockModel;
import com.sonamorningstar.eternalartifacts.client.model.RetexturedModel;
import com.sonamorningstar.eternalartifacts.client.renderer.FancyChestRenderer;
import com.sonamorningstar.eternalartifacts.content.entity.client.DemonEyeModel;
import com.sonamorningstar.eternalartifacts.content.entity.client.DemonEyeRenderer;
import com.sonamorningstar.eternalartifacts.content.entity.client.ModModelLayers;
import com.sonamorningstar.eternalartifacts.content.entity.client.PinkyRenderer;
import com.sonamorningstar.eternalartifacts.content.item.EncumbatorItem;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.content.fluid.BaseFluidType;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
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
        event.register(ModMenuTypes.BOOK_DUPLICATOR.get(), BookDuplicatorScreen::new);
    }

    @SubscribeEvent
    public static void registerColorHandlerItem(RegisterColorHandlersEvent.Item event) {
        event.register((pStack, pTintIndex) -> {
            if(pTintIndex == 1) return ((BaseFluidType) ModFluidTypes.NOUS.get()).getTintColor();
            else return -1;
        }, ModItems.NOUS_BUCKET.get());
        event.register((stack, ti) ->{
            BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
            return BlockColors.createDefault().getColor(blockstate, null, null, ti);
        }, ModBlocks.FOUR_LEAF_CLOVER.asItem());
        event.register(new RetexturedColor(), ModItems.GARDENING_POT.get());
        event.register(new RetexturedColor(), ModItems.FANCY_CHEST.get());
    }

    @SubscribeEvent
    public static void registerColorHandlerBlock(RegisterColorHandlersEvent.Block event) {
        event.register(new RetexturedColor(), ModBlocks.GARDENING_POT.get());
        event.register(new RetexturedColor(), ModBlocks.FANCY_CHEST.get());
        event.register((state, level, pos, ti) ->
            level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos)
                    : GrassColor.getDefaultColor()
        , ModBlocks.FOUR_LEAF_CLOVER.get());
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(new ResourceLocation(MODID, "colored_block"), ColoredBlockModel.LOADER);
        event.register(new ResourceLocation(MODID, "retextured"), RetexturedModel.LOADER);
    }

    @SubscribeEvent
    public static void fmlClient(FMLClientSetupEvent event) {
        event.enqueueWork( () ->
            ItemProperties.register(ModItems.ENCUMBATOR.get(),
                    new ResourceLocation(MODID, "active"), (s, l, e, sd) -> EncumbatorItem.isStackActive(s) ? 1.0F : 0.0F)
        );

        EntityRenderers.register(ModEntities.DEMON_EYE.get(), DemonEyeRenderer::new);
        EntityRenderers.register(ModEntities.PINKY.get(), PinkyRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.DEMON_EYE_LAYER, DemonEyeModel::createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.FANCY_CHEST_LAYER, FancyChestRenderer::createSingleBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FANCY_CHEST.get(), FancyChestRenderer::new);
    }



}