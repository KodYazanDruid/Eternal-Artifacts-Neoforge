package com.sonamorningstar.eternalartifacts;

import com.mojang.logging.LogUtils;
import com.sonamorningstar.eternalartifacts.content.block.GardeningPotBlock;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.network.Channel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
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
     * TODO: Geothermal Generator, Piezoelectric Plane
     * TODO: Modular Fluid Tank
     * TODO: A plant with infinite water supply
     * TODO: Configs.
     *
     * TODO: CLEAN UP CODE OMG ANVILINATOR BLOCK ENTITY IS A MESS
     *
    */

    public static final String MODID = "eternalartifacts";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
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
        ModSounds.SOUNDS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(Channel::onRegisterPayloadHandler);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}