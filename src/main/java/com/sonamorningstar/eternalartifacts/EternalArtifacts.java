package com.sonamorningstar.eternalartifacts;

import com.mojang.logging.LogUtils;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.event.hooks.ModHooks;
import com.sonamorningstar.eternalartifacts.network.Channel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import static net.neoforged.neoforge.common.NeoForgeMod.enableMilkFluid;

@Mod(EternalArtifacts.MODID)
public class EternalArtifacts {
    /**
     * TODO: Add proper power generation system (like biodiesel engine from IE, peat generator from cyclic(originally from forestry)) instead of burning oranges.
     * TODO: Add orange tree or add orange as a drop for acacia leaves.
     * TODO: Add disenchanter and other enchantment related blocks.
     * TODO: A storage block can storage items, fluids and energy dynamically.
     * TODO: Fourlegged model for gardening pot.
     * TODO: Decorative gardening pot with old model(maybe combine it with four legged)
     * TODO: Banana bread.
     * TODO: Piezoelectric Plane
     * TODO: Modular Fluid Tank
     * TODO: A plant with infinite water supply
     * TODO: Smart Piston (with sticky)
     * TODO: ARDITE ?
     * TODO: Configs.
     * TODO: Scrap boxes and similar loot thingies.
     * <p>
     * TODO: CLEAN UP CODE OMG ANVILINATOR BLOCK ENTITY IS A MESS
     *
    */

    public static final String MODID = "eternalartifacts";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ModHooks hooks = new ModHooks();

    public EternalArtifacts(IEventBus modEventBus) {
        enableMilkFluid();
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModLoots.FUNCTIONS.register(modEventBus);
        ModLoots.GLOBAL_MODIFIER.register(modEventBus);
        ModLoots.CONDITIONS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModPaintings.PAINTINGS.register(modEventBus);

        modEventBus.addListener(Channel::onRegisterPayloadHandler);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        hooks.construct(modEventBus);
    }

}