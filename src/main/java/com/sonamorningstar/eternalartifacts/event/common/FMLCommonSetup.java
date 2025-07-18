package com.sonamorningstar.eternalartifacts.event.common;

import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronDrainInteraction;
import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.api.machine.MachineEnchants;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModFluids;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModPotions;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.CutlassModifier;
import com.sonamorningstar.eternalartifacts.event.custom.charms.RegisterCharmAttributesEvent;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FMLCommonSetup {

    @SubscribeEvent
    public static void event(final FMLCommonSetupEvent event) {
        event.enqueueWork(()-> {
            ModLoader.get().postEvent(new RegisterCharmAttributesEvent(CharmStorage.itemAttributes));
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.TIGRIS_FLOWER.getId(), ModBlocks.POTTED_TIGRIS);
            registerCauldronContextsForItemFluidHandlers(ModItems.JAR.get());
            setupCauldronInteractions();
            setupCharmSlots();
            registerDispenserBehaviours();
            registerPotions();
            MachineEnchants.bootstrap();
            
            CutlassModifier.ENTITY_HEAD_MAP.put(EntityType.DROWNED, ModItems.DROWNED_HEAD.get());
            CutlassModifier.ENTITY_HEAD_MAP.put(EntityType.HUSK, ModItems.HUSK_HEAD.get());
            CutlassModifier.ENTITY_HEAD_MAP.put(EntityType.STRAY, ModItems.STRAY_SKULL.get());
            CutlassModifier.ENTITY_HEAD_MAP.put(EntityType.BLAZE, ModItems.BLAZE_HEAD.get());
            
            TesseractNetwork.CAPABILITY_NAMES.put(IEnergyStorage.class, ModConstants.ENERGY_CAPABILITY.translatable());
            TesseractNetwork.CAPABILITY_NAMES.put(IFluidHandler.class, ModConstants.FLUID_CAPABILITY.translatable());
            TesseractNetwork.CAPABILITY_NAMES.put(IItemHandler.class, ModConstants.ITEM_CAPABILITY.translatable());
        });

    }

    private static void registerCauldronContextsForItemFluidHandlers(Item item) {
        CauldronInteraction.EMPTY.map().put(item, ModCauldronInteraction.EMPTY);
        CauldronInteraction.WATER.map().put(item, ModCauldronDrainInteraction.WATER);
        CauldronInteraction.LAVA.map().put(item, ModCauldronDrainInteraction.LAVA);
        ModCauldronInteraction.PLASTIC.map().put(item, ModCauldronDrainInteraction.PLASTIC);
    }
    private static void setupCauldronInteractions() {
        ModCauldronInteraction.PLASTIC.map().put(Items.BUCKET, ModCauldronDrainInteraction.PLASTIC);
        CauldronInteraction.EMPTY.map().put(ModFluids.LIQUID_PLASTIC.getBucketItem(), ModCauldronInteraction.EMPTY);
        ModCauldronInteraction.PLASTIC.map().put(Items.BLUE_DYE, ModCauldronInteraction.DYE_PLASTIC);
    }
    private static void registerDispenserBehaviours() {
        DispenseItemBehavior dispenseitembehavior = new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource src, ItemStack stack) {
                this.setSuccess(ArmorItem.dispenseArmor(src, stack));
                return stack;
            }
        };
        
        DispenserBlock.registerBehavior(ModItems.DROWNED_HEAD, dispenseitembehavior);
        DispenserBlock.registerBehavior(ModItems.HUSK_HEAD, dispenseitembehavior);
        DispenserBlock.registerBehavior(ModItems.STRAY_SKULL, dispenseitembehavior);
        DispenserBlock.registerBehavior(ModItems.BLAZE_HEAD, dispenseitembehavior);
    }
    
    private static void registerPotions() {
        addPotionMix(ModPotions.ANGLERS_LUCK.get(), ModItems.ANCIENT_FRUIT.get(), ModPotions.LONG_ANGLERS_LUCK.get(), ModPotions.STRONG_ANGLERS_LUCK.get());
        addPotionMix(ModPotions.LURING.get(), ModItems.FROG_LEGS.get(), ModPotions.LONG_LURING.get(), ModPotions.STRONG_LURING.get());
    }
    
    private static void addPotionMix(Potion potion, Item input, Potion longV, Potion strongV) {
        PotionBrewing.addMix(Potions.AWKWARD, input, potion);
        PotionBrewing.addMix(potion, Items.REDSTONE, longV);
        PotionBrewing.addMix(potion, Items.GLOWSTONE_DUST, strongV);
    }

    private static void setupCharmSlots() {
        var map = CharmStorage.slotTypes;
        map.put(0, CharmType.HEAD);
        map.put(1, CharmType.NECKLACE);
        map.put(2, CharmType.RING);
        map.put(3, CharmType.RING);
        map.put(4, CharmType.BELT);
        map.put(5, CharmType.FEET);
        map.put(6, CharmType.HAND);
        map.put(7, CharmType.HAND);
        map.put(8, CharmType.BRACELET);
        map.put(9, CharmType.BACK);
        map.put(10, CharmType.CHARM);
        map.put(11, CharmType.CHARM);
    }

}
