package com.sonamorningstar.eternalartifacts;

import com.mojang.logging.LogUtils;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.morph.MobModelRenderer;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.core.structure.ModStructurePieces;
import com.sonamorningstar.eternalartifacts.core.structure.ModStructureTypes;
import com.sonamorningstar.eternalartifacts.event.custom.charms.CharmTickEvent;
import com.sonamorningstar.eternalartifacts.compat.ModHooks;
import com.sonamorningstar.eternalartifacts.network.Channel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import org.slf4j.Logger;

import static net.neoforged.neoforge.common.NeoForgeMod.enableMilkFluid;

@Mod(EternalArtifacts.MODID)
public class EternalArtifacts {
    /**
     * TODO: Add proper power generation system (like biodiesel engine from IE, peat generator from cyclic(originally from forestry)) instead of burning oranges.
     * TODO: Add disenchanter and other enchantment related blocks.
     * TODO: A storage block can storage items, fluids and energy. (battery box with items and fluids)
     * TODO: Fourlegged model for gardening pot.
     * TODO: Decorative gardening pot with old model(maybe combine it with four legged)
     * TODO: Piezoelectric Plane
     * TODO: Modular Fluid Tank
     * TODO: Smart Piston (with sticky)
     * TODO: Scrap boxes and similar loot thingies.
     * TODO: Shulker armor.
     * TODO: Camouflage armor.
     * TODO: Colored flower pots.
     * TODO: Add solar helmet.
     * TODO: Block pattern copy paste.
     * TODO: Add fluid hopper.
     * TODO: Add block placer/breaker.
    */

    public static final String MODID = "eternalartifacts";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EternalArtifacts(IEventBus modEventBus) {
        enableMilkFluid();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        ModSpells.SPELLS.register(modEventBus);
        ModMachines.MACHINES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModLoots.FUNCTIONS.register(modEventBus);
        ModLoots.GLOBAL_MODIFIER.register(modEventBus);
        ModLoots.CONDITIONS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModRecipes.RECIPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModPaintings.PAINTINGS.register(modEventBus);
        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModStructureTypes.STRUCTURE_TYPES.register(modEventBus);
        ModStructurePieces.PIECE_TYPE.register(modEventBus);
        ModInventoryTabs.INVENTORY_TABS.register(modEventBus);
        ModDataAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(RegisterCapabilitiesEvent.class, ModMachines.MACHINES::registerCapabilities);
        modEventBus.addListener(RegisterPayloadHandlerEvent.class, Channel::onRegisterPayloadHandler);
        NeoForge.EVENT_BUS.addListener(CharmTickEvent.class, CharmStorage::charmTick);
        ModCommands.addListener();
        NeoForge.EVENT_BUS.addListener(RenderPlayerEvent.Pre.class, MobModelRenderer::playerRenderPre);

        new ModHooks().construct(modEventBus);
    }
}