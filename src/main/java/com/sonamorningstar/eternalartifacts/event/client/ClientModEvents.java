package com.sonamorningstar.eternalartifacts.event.client;

import com.sonamorningstar.eternalartifacts.client.ColorUtils;
import com.sonamorningstar.eternalartifacts.client.RetexturedColor;
import com.sonamorningstar.eternalartifacts.client.gui.overlay.*;
import com.sonamorningstar.eternalartifacts.client.gui.screen.*;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.base.MultiBlockRenderer;
import com.sonamorningstar.eternalartifacts.client.render.entity.*;
import com.sonamorningstar.eternalartifacts.client.render.item.PortableBatteryLayer;
import com.sonamorningstar.eternalartifacts.client.render.item.PortableFurnaceLayer;
import com.sonamorningstar.eternalartifacts.client.resources.model.*;
import com.sonamorningstar.eternalartifacts.client.render.blockentity.*;
import com.sonamorningstar.eternalartifacts.client.shader.SpellShaders;
import com.sonamorningstar.eternalartifacts.content.entity.client.*;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.CutlassModifier;
import com.sonamorningstar.eternalartifacts.event.custom.RegisterTabHoldersEvent;
import com.sonamorningstar.eternalartifacts.event.custom.RegisterUnrenderableOverridesEvent;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

import java.util.Objects;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.ANVILINATOR.get(), AnvilinatorScreen::new);
        event.register(ModMenuTypes.BIOFURNACE.get(), BioFurnaceScreen::new);
        event.register(ModMenuTypes.BOOK_DUPLICATOR.get(), BookDuplicatorScreen::new);
        event.register(ModMenuTypes.BATTERY_BOX.get(), BatteryBoxScreen::new);
        event.register(ModMenuTypes.DYNAMO_MENU.get(), FluidDynamoScreen::new);
        event.register(ModMenuTypes.KNAPSACK.get(), KnapsackScreen::new);
        event.register(ModMenuTypes.NOUS_TANK.get(), NousTankScreen::new);
        event.register(ModMenuTypes.TANK_KNAPSACK.get(), TankKnapsackScreen::new);
        event.register(ModMenuTypes.TESSERACT.get(), TesseractScreen::new);
        event.register(ModMenuTypes.SOLID_DYNAMO.get(), SolidDynamoScreen::new);
    }

    @SubscribeEvent
    public static void registerTabHolder(RegisterTabHoldersEvent event) {
        event.register(ModItems.KNAPSACK.get(), ModInventoryTabs.KNAPSACK.get());
        event.register(ModItems.ENDER_KNAPSACK.get(), ModInventoryTabs.ENDER_KNAPSACK.get());
        event.register(ModItems.TANK_KNAPSACK.get(), ModInventoryTabs.TANK_KNAPSACK.get());
        event.register(ModItems.PORTABLE_CRAFTER.get(), ModInventoryTabs.CRAFTER.get());
        event.register(Items.COD, ModInventoryTabs.FISH_TAB.get());
        event.register(ModItems.PORTABLE_BATTERY.get(), ModInventoryTabs.PORTABLE_BATTERY.get());
    }

    @SubscribeEvent
    public static void registerUnrenderableOverrides(RegisterUnrenderableOverridesEvent event) {
        event.register(EquipmentSlot.HEAD, ModTags.Items.SHULKER_SHELL);
        event.register(EquipmentSlot.HEAD, Items.BUCKET);
        event.register(EquipmentSlot.HEAD, Items.PLAYER_HEAD);
        event.register(EquipmentSlot.HEAD, ModItems.DEATH_CAP.get());
        CutlassModifier.ENTITY_HEAD_MAP.values().forEach(item -> {
            if(item != Items.DRAGON_HEAD) event.register(EquipmentSlot.HEAD, item);
        });
    }
    
    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        SpellShaders.registerShaders(event);
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
        event.register(ColorUtils::getColorFromNBT, ModItems.LIGHTSABER);
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
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "compass_overlay"), new CompassOverlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "recovery_compass_overlay"), new RecoveryCompassOverlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "clock_overlay"), new ClockOverlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "map_overlay"), new MapOverlay());
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(MODID, "hammering_recipe_overlay"), new HammeringRecipeOverlay());
    }
    
    @SubscribeEvent
    public static void registerSkullModelLayers(EntityRenderersEvent.CreateSkullModels event) {
        EntityModelSet modelSet = event.getEntityModelSet();
        event.registerSkullModel(ModSkullType.DROWNED,
            new TwoLayerSkullModel(
                ModSkullType.DROWNED,
                modelSet.bakeLayer(ModModelLayers.DROWNED_HEAD),
                modelSet.bakeLayer(ModModelLayers.DROWNED_HEAD_OVERLAY)
            )
        );
        event.registerSkullModel(ModSkullType.HUSK, new SkullModel(modelSet.bakeLayer(ModModelLayers.HUSK_HEAD)));
        event.registerSkullModel(ModSkullType.STRAY,
            new TwoLayerSkullModel(
                ModSkullType.STRAY,
                modelSet.bakeLayer(ModModelLayers.STRAY_SKULL),
                modelSet.bakeLayer(ModModelLayers.STRAY_SKULL_OVERLAY)
            )
        );
        event.registerSkullModel(ModSkullType.BLAZE, new SkullModel(modelSet.bakeLayer(ModModelLayers.BLAZE_HEAD)));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.DEMON_EYE_LAYER, DemonEyeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.DUCK_LAYER, DuckModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.TORNADO_LAYER, TornadoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.METEORITE_LAYER, MeteoriteModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SPELL_TOME_LAYER, SpellTomeModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CHARGED_SHEEP_SWIRL, () -> ChargedSheepRenderer.createFurSwirlLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(ModModelLayers.MISSILE_LAYER, MissileModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.PORTABLE_BATTERY_LAYER, PortableBatteryModel::createLayer);
        event.registerLayerDefinition(ModModelLayers.BUCKET_LAYER, BucketHeadModel::createLayer);

        //event.registerLayerDefinition(ModModelLayers.FANCY_CHEST_LAYER, FancyChestRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.JAR_LAYER, JarRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.DYNAMO_LAYER, DynamoModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.NOUS_TANK_LAYER, NousTankRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.OIL_REFINERY_LAYER, OilRefineryRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ENERGY_DOCK_LAYER, EnergyDockBlockEntityRenderer::createSingleBodyLayer);
        
        event.registerLayerDefinition(ModModelLayers.DROWNED_HEAD, () -> TwoLayerSkullModel.createBaseLayer(64, 64));
        event.registerLayerDefinition(ModModelLayers.DROWNED_HEAD_OVERLAY, () -> TwoLayerSkullModel.createOverlayLayer(64, 64));
        event.registerLayerDefinition(ModModelLayers.HUSK_HEAD, () -> TwoLayerSkullModel.createBaseLayer(64, 64));
        event.registerLayerDefinition(ModModelLayers.STRAY_SKULL, () -> TwoLayerSkullModel.createBaseLayer(64, 32));
        event.registerLayerDefinition(ModModelLayers.STRAY_SKULL_OVERLAY, () -> TwoLayerSkullModel.createOverlayLayer(64, 32));
        event.registerLayerDefinition(ModModelLayers.BLAZE_HEAD, () -> TwoLayerSkullModel.createBaseLayer(64, 32));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.FANCY_CHEST.get(), FancyChestRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.JAR.get(), JarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), DynamoRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOLID_COMBUSTION_DYNAMO.get(), DynamoRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NOUS_TANK.get(), NousTankRenderer::new);
        event.registerBlockEntityRenderer(ModMachines.OIL_REFINERY.getBlockEntity(), OilRefineryRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ENERGY_DOCK.get(), EnergyDockBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TESSERACT.get(), ctx -> new TesseractRenderer());
        event.registerBlockEntityRenderer(ModBlockEntities.SKULL.get(), ModSkullBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MACHINE_WORKBENCH.get(), MachineWorkbenchRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PICTURE_SCREEN.get(), PictureScreenRenderer::new);

        event.registerBlockEntityRenderer(ModMachines.MOB_LIQUIFIER.getBlockEntity(), ctx -> new AreaRenderer<>());

        event.registerBlockEntityRenderer(ModMultiblocks.PUMPJACK.getBlockEntity(), MultiBlockRenderer::new);
        event.registerBlockEntityRenderer(ModMultiblocks.GENERATOR.getBlockEntity(), MultiBlockRenderer::new);
        
        event.registerEntityRenderer(ModEntities.DEMON_EYE.get(), DemonEyeRenderer::new);
        event.registerEntityRenderer(ModEntities.PINKY.get(), PinkyRenderer::new);
        event.registerEntityRenderer(ModEntities.CHARGED_SHEEP.get(), ChargedSheepRenderer::new);
        event.registerEntityRenderer(ModEntities.DUCK.get(), DuckRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGICAL_BOOK.get(), MagicalBookRenderer::new);
        event.registerEntityRenderer(ModEntities.PRIMED_BLOCK.get(), TntRenderer::new);
        event.registerEntityRenderer(ModEntities.TORNADO.get(), TornadoRenderer::new);
        event.registerEntityRenderer(ModEntities.METEORITE.get(), MeteoriteRenderer::new);
        event.registerEntityRenderer(ModEntities.MISSILE.get(), MissileRenderer::new);
        event.registerEntityRenderer(ModEntities.SPELL_PROJECTILE.get(), SpellProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.AMETHYST_ARROW.get(), AmethystArrowRenderer::new);
        event.registerEntityRenderer(ModEntities.HONEY_SLIME.get(), HoneySlimeRenderer::new);
    }

    @SubscribeEvent
    public static void attachLayers(EntityRenderersEvent.AddLayers event) {
        EntityRendererProvider.Context ctx = event.getContext();
        for (EntityType<?> type : event.getEntityTypes()) {
            EntityRenderer<?> renderer = event.getRenderer(type);
            if (renderer instanceof LivingEntityRenderer<?,?> ler) attachLayers(ler, ctx);
        }
        event.getSkins().forEach(renderer ->{
            LivingEntityRenderer<Player, EntityModel<Player>> skin = event.getSkin(renderer);
            attachLayers(Objects.requireNonNull(skin), ctx);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity, M extends EntityModel<T>> void attachLayers(LivingEntityRenderer<T, M> renderer, EntityRendererProvider.Context ctx) {
        /*renderer.addLayer(new HolyDaggerLayer<>(renderer));
        renderer.addLayer(new ProtectiveAuraLayer<>(renderer));*/
        
        if (renderer instanceof HumanoidMobRenderer<?, ?> hmr) {
            renderer.addLayer(new HolyDaggerLayer<>(renderer));
            renderer.addLayer(new ProtectiveAuraLayer<>(renderer));
            renderer.addLayer((RenderLayer<T, M>) new ShulkerShellLayer<>(hmr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new BucketLayer<>(hmr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new PortableBatteryLayer<>(hmr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new PortableFurnaceLayer<>(hmr));
        }
        if (renderer instanceof PlayerRenderer pr) {
            renderer.addLayer(new HolyDaggerLayer<>(renderer));
            renderer.addLayer(new ProtectiveAuraLayer<>(renderer));
            renderer.addLayer((RenderLayer<T, M>) new ShulkerShellLayer<>(pr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new BucketLayer<>(pr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new PortableBatteryLayer<>(pr, ctx));
            renderer.addLayer((RenderLayer<T, M>) new PortableFurnaceLayer<>(pr));
        }
    }

}