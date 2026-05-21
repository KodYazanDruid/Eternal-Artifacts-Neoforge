package com.sonamorningstar.eternalartifacts.event.common;

import com.google.common.collect.HashMultimap;
import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronDrainInteraction;
import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.api.farm.FarmBehaviorRegistry;
import com.sonamorningstar.eternalartifacts.api.item.armorset.ArmorSetRegistry;
import com.sonamorningstar.eternalartifacts.api.item.armorset.ArmorSets;
import com.sonamorningstar.eternalartifacts.api.item.armorset.sets.base.ArmorSet;
import com.sonamorningstar.eternalartifacts.api.item.armorset.sets.base.AttributeArmorSet;
import com.sonamorningstar.eternalartifacts.api.machine.MachineEnchants;
import com.sonamorningstar.eternalartifacts.api.machine.tesseract.TesseractNetwork;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.AddRandomCharmModifier;
import com.sonamorningstar.eternalartifacts.data.loot.modifier.CutlassModifier;
import com.sonamorningstar.eternalartifacts.event.custom.RegisterFarmBehaviorEvent;
import com.sonamorningstar.eternalartifacts.event.custom.RegisterMultiblockPatternsEvent;
import com.sonamorningstar.eternalartifacts.event.custom.charms.RegisterCharmAttributesEvent;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.UUID;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class
FMLCommonSetup {

    @SubscribeEvent
    public static void event(final FMLCommonSetupEvent event) {
        event.enqueueWork(()-> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.TIGRIS_FLOWER.getId(), ModBlocks.POTTED_TIGRIS);
            registerCauldronContextsForItemFluidHandlers(ModItems.JAR.get());
            
            setupCauldronInteractions();
            registerDispenserBehaviours();
            registerPotions();
            FarmBehaviorRegistry.bootstrap();
            
            CutlassModifier.ENTITY_HEAD_MAP.put(EntityType.DROWNED, ModItems.DROWNED_HEAD.get());
            CutlassModifier.ENTITY_HEAD_MAP.put(EntityType.HUSK, ModItems.HUSK_HEAD.get());
            CutlassModifier.ENTITY_HEAD_MAP.put(EntityType.STRAY, ModItems.STRAY_SKULL.get());
            CutlassModifier.ENTITY_HEAD_MAP.put(EntityType.BLAZE, ModItems.BLAZE_HEAD.get());
            CutlassModifier.ENTITY_HEAD_MAP.put(ModEntities.SOUL_BLAZE.get(), ModItems.SOUL_BLAZE_HEAD.get());
            
            TesseractNetwork.CAPABILITY_NAMES.put(IEnergyStorage.class, ModConstants.ENERGY_CAPABILITY.translatable());
            TesseractNetwork.CAPABILITY_NAMES.put(IFluidHandler.class, ModConstants.FLUID_CAPABILITY.translatable());
            TesseractNetwork.CAPABILITY_NAMES.put(IItemHandler.class, ModConstants.ITEM_CAPABILITY.translatable());
            ModLoader.get().postEvent(new RegisterCharmAttributesEvent(CharmStorage.itemAttributes));
            ModLoader.get().postEvent(new RegisterFarmBehaviorEvent());
            ModLoader.get().postEvent(new RegisterMultiblockPatternsEvent(Multiblock.PATTERNS));
            
            ArmorSetRegistry.registerArmorSetBonus(new ArmorSet(ArmorSets.CACTUS_ARMOR, List.of(
                ModItems.CACTUS_HELMET.get(),
                ModItems.CACTUS_CHESTPLATE.get(),
                ModItems.CACTUS_LEGGINGS.get(),
                ModItems.CACTUS_BOOTS.get()
            )), living -> {});
            
            AttributeModifier steelArmor = new AttributeModifier(UUID.fromString("e1d8cd55-4b29-47a9-afc0-507f82c846b4"),"Eternal Artifacts Steel Armor Bonus", 2.0, AttributeModifier.Operation.ADDITION);
            AttributeModifier steelArmorToughness = new AttributeModifier(UUID.fromString("180a53c9-7d14-4b65-885b-05284d768edf"),"Eternal Artifacts Steel Armor Toughness Bonus", 2.0, AttributeModifier.Operation.ADDITION);
            HashMultimap<Attribute, AttributeModifier> steelArmorAttributes = HashMultimap.create();
            steelArmorAttributes.put(Attributes.ARMOR, steelArmor);
            steelArmorAttributes.put(Attributes.ARMOR_TOUGHNESS, steelArmorToughness);
            AttributeArmorSet steelSetBonus = new AttributeArmorSet(ArmorSets.STEEL_ARMOR, List.of(
                ModItems.STEEL_HELMET.get(),
                ModItems.STEEL_CHESTPLATE.get(),
                ModItems.STEEL_LEGGINGS.get(),
                ModItems.STEEL_BOOTS.get()
            ), steelArmorAttributes);
            ArmorSetRegistry.registerArmorSetBonus(steelSetBonus, living -> {});
            
            AttributeModifier shulkerKnockRes = new AttributeModifier(UUID.fromString("3b8ab916-7eb4-486c-9c3c-9b05f82ede12"), "Eternal Artifacts Knockback Resistance", 0.5, AttributeModifier.Operation.ADDITION);
            HashMultimap<Attribute, AttributeModifier> shulkerArmorAttributes = HashMultimap.create();
            shulkerArmorAttributes.put(Attributes.KNOCKBACK_RESISTANCE, shulkerKnockRes);
            ArmorSetRegistry.registerArmorSetBonus(new AttributeArmorSet(ArmorSets.SHULKER_ARMOR, List.of(
                ModItems.SHULKER_HELMET.get(),
                ModItems.SHULKER_CHESTPLATE.get(),
                ModItems.SHULKER_LEGGINGS.get(),
                ModItems.SHULKER_BOOTS.get()
            ), shulkerArmorAttributes), living -> {});
            
        });
        
        MachineEnchants.bootstrap();
        setupCharmSlots();
        
        AddRandomCharmModifier.CHARM_ITEMS = List.of(
            ModItems.POWER_GAUNTLET.get(),
            ModItems.HEART_NECKLACE.get(),
            ModItems.SAGES_TALISMAN.get(),
            ModItems.BAND_OF_ARCANE.get(),
            ModItems.EMERALD_SIGNET.get(),
            ModItems.SKYBOUND_TREADS.get(),
            ModItems.GALE_SASH.get(),
            ModItems.MAGIC_QUIVER.get(),
            ModItems.IRON_LEATHER_GLOVES.get(),
            ModItems.FINAL_CUT.get(),
            ModItems.ODDLY_SHAPED_OPAL.get(),
            ModItems.RAINCOAT.get(),
            ModItems.MAGIC_BANE.get(),
            ModItems.DEATH_CAP.get(),
            ModItems.MOONGLASS_PENDANT.get(),
            ModItems.HOLY_DAGGER.get(),
            ModItems.FROG_LEGS.get(),
            ModItems.MEDKIT.get(),
            ModItems.MAGIC_FEATHER.get(),
            ModItems.COMFY_SHOES.get(),
            ModItems.MAGNET.get(),
            ModBlocks.RESONATOR.asItem(),
            ModItems.LIGHTSABER.get()
        );
        
        VoxelShape pumpjackShape = Shapes.box(0, 0, 0, 3, 0.5, 5);
        VoxelShape leftPole = Shapes.box(0.25, 0.5, 2.25, 0.75, 3, 2.75);
        VoxelShape rightPole = Shapes.box(2.25, 0.5, 2.25, 2.75, 3, 2.75);
        VoxelShape fluidPort = Shapes.box(1, 0, 0, 2, 1, 1);
        VoxelShape energyPort = Shapes.box(1, 0, 4, 2, 1, 5);
        VoxelShape fullShape = Shapes.or(pumpjackShape, leftPole, rightPole, fluidPort, energyPort);
        ModMultiblocks.PUMPJACK.getMultiblock().setFullShape(fullShape);
        VoxelShape minerShape = Shapes.box(0, 0, 0, 5, 0.5, 5);
        VoxelShape pole1 = Shapes.box(0.25, 0.5, 0.25, 0.75, 2, 0.75);
        VoxelShape pole2 = Shapes.box(0.25, 0.5, 4.25, 0.75, 2, 4.75);
        VoxelShape pole3 = Shapes.box(4.25, 0.5, 4.25, 4.75, 2, 4.75);
        VoxelShape pole4 = Shapes.box(4.25, 0.5, 0.25, 4.75, 2, 0.75);
        VoxelShape master = Shapes.box(2, 0, 0, 3, 1, 1);
        VoxelShape energy = Shapes.box(2, 0, 4, 3, 1, 5);
        VoxelShape storage = Shapes.box(4, 0, 2, 5, 1, 3);
        VoxelShape tank = Shapes.box(0, 0, 2, 1, 1, 3);
        VoxelShape center = Shapes.box(2, 0.5, 2, 3, 2, 3);
        VoxelShape fullMinerShape = Shapes.or(minerShape, pole1, pole2, pole3, pole4, master, energy, storage, tank, center);
        ModMultiblocks.CHUNK_EATER.getMultiblock().setFullShape(fullMinerShape);
        
    }

    private static void registerCauldronContextsForItemFluidHandlers(Item item) {
        CauldronInteraction.EMPTY.map().put(item, ModCauldronInteraction.EMPTY);
        CauldronInteraction.WATER.map().put(item, ModCauldronDrainInteraction.WATER);
        CauldronInteraction.LAVA.map().put(item, ModCauldronDrainInteraction.LAVA);
        ModCauldronInteraction.PLASTIC.map().put(item, ModCauldronDrainInteraction.PLASTIC);
        ModCauldronInteraction.CRUDE_OIL.map().put(item, ModCauldronDrainInteraction.CRUDE_OIL);
        ModCauldronInteraction.NAPHTHA.map().put(item, ModCauldronDrainInteraction.NAPHTHA);
    }
    private static void setupCauldronInteractions() {
        ModCauldronInteraction.PLASTIC.map().put(Items.BUCKET, ModCauldronDrainInteraction.PLASTIC);
        CauldronInteraction.EMPTY.map().put(ModFluids.LIQUID_PLASTIC.getBucketItem(), ModCauldronInteraction.EMPTY);
        ModCauldronInteraction.CRUDE_OIL.map().put(Items.BUCKET, ModCauldronDrainInteraction.CRUDE_OIL);
        CauldronInteraction.EMPTY.map().put(ModFluids.CRUDE_OIL.getBucketItem(), ModCauldronInteraction.EMPTY);
        ModCauldronInteraction.NAPHTHA.map().put(Items.BUCKET, ModCauldronDrainInteraction.NAPHTHA);
        CauldronInteraction.EMPTY.map().put(ModFluids.NAPHTHA.getBucketItem(), ModCauldronInteraction.EMPTY);
        
        ModCauldronInteraction.PLASTIC.map().put(Items.BLUE_DYE, ModCauldronInteraction.DYE_PLASTIC);
        // Tag-based interactions moved to ServerStartingEvent in CommonEvents
        // because tags are not loaded yet during FMLCommonSetupEvent
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
        DispenserBlock.registerBehavior(ModItems.SOUL_BLAZE_HEAD, dispenseitembehavior);
    }
    
    private static void registerPotions() {
        addPotionMix(ModPotions.ANGLERS_LUCK.get(), ModItems.ANCIENT_FRUIT.get(), ModPotions.LONG_ANGLERS_LUCK.get(), ModPotions.STRONG_ANGLERS_LUCK.get());
        addPotionMix(ModPotions.LURING.get(), ModItems.FROG_LEGS.get(), ModPotions.LONG_LURING.get(), ModPotions.STRONG_LURING.get());
        addPotionMix(ModPotions.ENDURANCE.get(), ModItems.GLOW_INK_DUST.get(), ModPotions.LONG_ENDURANCE.get(), ModPotions.STRONG_ENDURANCE.get());
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
