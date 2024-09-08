package com.sonamorningstar.eternalartifacts.event;

import com.sonamorningstar.eternalartifacts.client.RetexturedColor;
import com.sonamorningstar.eternalartifacts.client.gui.screen.*;
import com.sonamorningstar.eternalartifacts.client.resources.model.ColoredBlockModel;
import com.sonamorningstar.eternalartifacts.client.resources.model.FluidCombustionDynamoModel;
import com.sonamorningstar.eternalartifacts.client.resources.model.RetexturedModel;
import com.sonamorningstar.eternalartifacts.client.renderer.blockentity.*;
import com.sonamorningstar.eternalartifacts.client.renderer.entity.HolyDaggerLayer;
import com.sonamorningstar.eternalartifacts.content.entity.client.*;
import com.sonamorningstar.eternalartifacts.content.item.EncumbatorItem;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

import java.util.Objects;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.ANVILINATOR.get(), AnvilinatorScreen::new);
        event.register(ModMenuTypes.BIOFURNACE.get(), BioFurnaceScreen::new);
        event.register(ModMenuTypes.BOOK_DUPLICATOR.get(), BookDuplicatorScreen::new);
        event.register(ModMenuTypes.BATTERY_BOX.get(), BatteryBoxScreen::new);
        event.register(ModMenuTypes.FLUID_COMBUSTION_MENU.get(), FluidCombustionScreen::new);
        event.register(ModMenuTypes.KNAPSACK.get(), KnapsackScreen::new);
        event.register(ModMenuTypes.NOUS_TANK.get(), NousTankScreen::new);
    }

    @SubscribeEvent
    public static void registerColorHandlerItem(RegisterColorHandlersEvent.Item event) {
        event.register((stack, ti) ->{
            BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
            return BlockColors.createDefault().getColor(blockstate, null, null, ti);
        }, ModBlocks.FOUR_LEAF_CLOVER.asItem());
        event.register(new RetexturedColor(), ModItems.GARDENING_POT.get());
        event.register(new RetexturedColor(), ModItems.FANCY_CHEST.get());
        ModFluids.FLUIDS.getEntries().forEach(holder -> {
            if (ModFluids.FLUIDS.isGeneric(holder)) event.register((stack, ti) -> ti == 1 ? holder.getTintColor() : 0xFFFFFFFF, holder.getBucketItem());
        });
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
        EntityRenderers.register(ModEntities.DUCK.get(), DuckRenderer::new);
        EntityRenderers.register(ModEntities.MAGICAL_BOOK.get(), MagicalBookRenderer::new);
        EntityRenderers.register(ModEntities.PRIMED_BLOCK.get(), TntRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.DEMON_EYE_LAYER, DemonEyeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.DUCK_LAYER, DuckModel::createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.FANCY_CHEST_LAYER, FancyChestRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.JAR_LAYER, JarRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.FLUID_COMBUSTION_LAYER, FluidCombustionDynamoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.NOUS_TANK_LAYER, NousTankRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.OIL_REFINERY_LAYER, OilRefineryRenderer::createSingleBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FANCY_CHEST.get(), FancyChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.JAR.get(), JarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), FluidCombustionRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NOUS_TANK.get(), NousTankRenderer::new);
        event.registerBlockEntityRenderer(ModMachines.OIL_REFINERY.getBlockEntity(), OilRefineryRenderer::new);
    }

    @SubscribeEvent
    public static void attachLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> type : event.getEntityTypes()) {
            EntityRenderer<?> renderer = event.getRenderer(type);
            if(renderer instanceof LivingEntityRenderer<?, ?> livingRenderer) attachRenderLayers(livingRenderer);
        }
        event.getSkins().forEach(renderer ->{
            LivingEntityRenderer<Player, EntityModel<Player>> skin = event.getSkin(renderer);
            attachRenderLayers(Objects.requireNonNull(skin));
        });
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void attachRenderLayers(LivingEntityRenderer<T, M> renderer) {
        renderer.addLayer(new HolyDaggerLayer<>(renderer));
    }



}