package com.sonamorningstar.eternalartifacts.event.common;

import com.sonamorningstar.eternalartifacts.api.charm.CharmAttributes;
import com.sonamorningstar.eternalartifacts.api.charm.CharmType;
import com.sonamorningstar.eternalartifacts.api.forceload.ForceLoadManager;
import com.sonamorningstar.eternalartifacts.api.item.decorator.BlueprintDecorator;
import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.capabilities.energy.MachineItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.energy.TesseractEnergyCap;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.*;
import com.sonamorningstar.eternalartifacts.capabilities.item.MachineItemItemStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModScaleableItemItemStorage;
import com.sonamorningstar.eternalartifacts.capabilities.item.TesseractInventoryCap;
import com.sonamorningstar.eternalartifacts.content.block.DrumBlock;
import com.sonamorningstar.eternalartifacts.content.block.FancyChestBlock;
import com.sonamorningstar.eternalartifacts.content.block.entity.EnergyDockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.block.entity.SolarPanel;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractMultiblockBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.*;
import com.sonamorningstar.eternalartifacts.content.item.ComfyShoesItem;
import com.sonamorningstar.eternalartifacts.content.multiblock.base.Multiblock;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.event.custom.charms.RegisterCharmAttributesEvent;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.util.CapabilityHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        ModFluids.FLUIDS.forEachBucketEntry(holder -> event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), holder.get()));

        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> CapabilityHelper.regItemEnergyCap(stack,50000,2500), ModItems.BATTERY.get());
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> CapabilityHelper.regItemEnergyCap(stack,75000,15000), ModItems.PORTABLE_BATTERY.get());
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> CapabilityHelper.regItemEnergyCap(stack,25000,5000), ModItems.LIGHTSABER.get());
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> CapabilityHelper.regItemEnergyCap(stack,10000,500), ModItems.CONFIGURATION_DRIVE.get());
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> CapabilityHelper.regItemEnergyCap(stack,16000,500), ModItems.PORTABLE_FURNACE.get());

        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack(stack, 1000), ModItems.JAR.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack(stack, Integer.MAX_VALUE), ModBlocks.NOUS_TANK.asItem());
        registerDrum(event, ModBlocks.COPPER_DRUM);
        registerDrum(event, ModBlocks.IRON_DRUM);
        registerDrum(event, ModBlocks.GOLD_DRUM);
        registerDrum(event, ModBlocks.STEEL_DRUM);
        registerDrum(event, ModBlocks.DIAMOND_DRUM);
        registerDrum(event, ModBlocks.NETHERITE_DRUM);

        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ModScaleableItemItemStorage(stack, ModEnchantments.VOLUME.get(), 9), ModItems.KNAPSACK.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> {
            int volume = stack.getEnchantmentLevel(ModEnchantments.VOLUME.get());
            List<ModFluidStorage> tankList = new ArrayList<>();
            for (int i = 0; i < (1 + volume) * 9; i++) {
                tankList.add(new ModFluidStorage(8000));
            }
            return new ModItemMultiFluidTank<>(stack, tankList);
        }, ModItems.TANK_KNAPSACK.get());

        event.registerItem(ModCapabilities.NutritionStorage.ITEM, (stack, ctx) -> new ItemNutritionStorage(stack), ModItems.FEEDING_CANISTER.get());
        event.registerBlockEntity(ModCapabilities.Heat.BLOCK, ModMachines.INDUCTION_FURNACE.getBlockEntity(), (be, ctx) -> be.heat);
        event.registerItem(ModCapabilities.ItemCooldown.ITEM, (stack, ctx) -> new ItemStackCooldown(stack, 200), ModItems.WRENCH);
        event.registerItem(Capabilities.FluidHandler.ITEM, InfiniteWaterTank::createForItem, ModBlocks.TIGRIS_FLOWER.asItem());
        event.registerEntity(Capabilities.EnergyStorage.ENTITY, ModEntities.CHARGED_SHEEP.get(), (entity, ctx) -> entity.energy);
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> CapabilityHelper.regItemEnergyCap(stack, 8000, 100), ModItems.SOLAR_PANEL_HELMET.get());

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.RESONATOR.get(), (be, ctx) ->
                be.getBlockState().getValue(BlockStateProperties.FACING) == ctx ? be.energy : null);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.JAR.get(), (be, ctx) -> be.tank);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.ENERGY_DOCK.get(), EnergyDockBlockEntity::getEnergy);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SHOCK_ABSORBER.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SOLAR_PANEL.get(), SolarPanel::createEnergyCap);
        registerMachineItem(event, ModBlocks.SHOCK_ABSORBER);
        registerMachineItem(event, ModBlocks.FLUID_COMBUSTION_DYNAMO);
        registerMachineItem(event, ModBlocks.BIOFURNACE);
        registerMachineItem(event, ModBlocks.BOOK_DUPLICATOR);
        registerMachineItem(event, ModBlocks.BATTERY_BOX);
        registerMachineItem(event, ModBlocks.ANVILINATOR);
        registerMachineItem(event, ModBlocks.SOLID_COMBUSTION_DYNAMO);
        registerMachineItem(event, ModBlocks.SOLAR_PANEL);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BIOFURNACE.get(), (be, context) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BIOFURNACE.get(), (be, context) -> be.inventory);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> CapabilityHelper.regSidedItemCaps(be, be.inventory, ctx, List.of(1)));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> CapabilityHelper.regSidedFluidCaps(be, be.tank, ctx));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.ANVILINATOR.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ANVILINATOR.get(), (be, ctx) -> CapabilityHelper.regSidedItemCaps(be, be.inventory, ctx, List.of(1)));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.ANVILINATOR.get(), (be, ctx) -> CapabilityHelper.regSidedFluidCaps(be, be.tank, ctx));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BATTERY_BOX.get(), (be, ctx) -> CapabilityHelper.regSidedEnergyCaps(be, be.energy, ctx));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BATTERY_BOX.get(), (be, ctx) -> CapabilityHelper.regSidedItemCaps(be, be.inventory, ctx, null));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), (be, ctx) ->
            ctx == null ? be.energy : be.getBlockState().getValue(BlockStateProperties.FACING) == ctx ? be.energy : null);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), (be, ctx) -> be.tank);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SOLID_COMBUSTION_DYNAMO.get(), (be, ctx) ->
            ctx == null ? be.energy : be.getBlockState().getValue(BlockStateProperties.FACING) == ctx ? be.energy : null);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SOLID_COMBUSTION_DYNAMO.get(), (be, ctx) -> be.inventory);
        
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.NOUS_TANK.get(), (be, ctx) -> CapabilityHelper.regSidedFluidCaps(be, be.tank, ctx));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BLUE_PLASTIC_CAULDRON.get(), (be, ctx) -> be.inventory);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.DRUM.get(), (be, ctx) -> be.tank);
        event.registerBlock(Capabilities.ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> {
            Container container = FancyChestBlock.getContainer((ChestBlock) state.getBlock(), state, level, pos, true);
            return container == null ? null : new InvWrapper(container);
        }, ModBlocks.FANCY_CHEST.get());

        event.registerBlock(Capabilities.FluidHandler.BLOCK,
                (level, pos, state, blockEntity, context) -> InfiniteWaterTank.INSTANCE,
                ModBlocks.TIGRIS_FLOWER.get(), ModBlocks.POTTED_TIGRIS.get());
        event.registerBlock(Capabilities.FluidHandler.BLOCK, (level, pos, state, blockEntity, context) -> {
            boolean flag = level.getBlockState(pos.above()).is(ModBlocks.TIGRIS_FLOWER);
            return flag ? InfiniteWaterTank.INSTANCE : null;
        }, ModBlocks.GARDENING_POT.get());
        
        event.registerBlock(Capabilities.FluidHandler.BLOCK, TrashCanHandler::registerCapability, ModBlocks.TRASH_CAN.get());
        event.registerBlock(Capabilities.ItemHandler.BLOCK, TrashCanHandler::registerCapability, ModBlocks.TRASH_CAN.get());
        event.registerBlock(Capabilities.EnergyStorage.BLOCK, TrashCanHandler::registerCapability, ModBlocks.TRASH_CAN.get());
        
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MACHINE_WORKBENCH.get(), (be, ctx) -> be.inventory);
    
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.TESSERACT.get(), (be, dir) -> {
            var network = be.getCachedTesseractNetwork();
            if (network == null) return null;
            return network.getCapabilityClass() == IItemHandler.class ? new TesseractInventoryCap(be) : null;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.TESSERACT.get(), (be, dir) -> {
            var network = be.getCachedTesseractNetwork();
            if (network == null) return null;
            return network.getCapabilityClass() == IFluidHandler.class ? new TesseractFluidCap(be) : null;
        });
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.TESSERACT.get(), (be, dir) -> {
            var network = be.getCachedTesseractNetwork();
            if (network == null) return null;
            return network.getCapabilityClass() == IEnergyStorage.class ? new TesseractEnergyCap(be) : null;
        });
        
        for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> holder : ModBlockEntities.MULTIBLOCKS) {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, holder.get(), (be, ctx) -> {
                if (be instanceof AbstractMultiblockBlockEntity ambe) {
                    return ambe.getEnergy(ctx);
                }
                return null;
            });
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, holder.get(), (be, ctx) -> {
                if (be instanceof AbstractMultiblockBlockEntity ambe) {
                    return ambe.getTank(ctx);
                }
                return null;
            });
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, holder.get(), (be, ctx) -> {
                if (be instanceof AbstractMultiblockBlockEntity ambe) {
                    return ambe.getInventory(ctx);
                }
                return null;
            });
        }
    }

    private static void registerDrum(RegisterCapabilitiesEvent event, DeferredBlock<DrumBlock> holder) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> {
            int volume = stack.getEnchantmentLevel(ModEnchantments.VOLUME.get());
            return new FluidHandlerItemStack(stack, holder.get().getCapacity() * (1 + volume));
        }, holder.asItem());
    }
    private static void registerMachineItem(RegisterCapabilitiesEvent event, DeferredBlock<?> holder) {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> new MachineItemEnergyStorage(stack), holder.asItem());
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new MachineItemItemStorage(stack), holder.asItem());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new MachineItemFluidStorage(stack), holder.asItem());
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DEMON_EYE.get(), DemonEyeEntity.createAttributes().build());
        event.put(ModEntities.PINKY.get(), PinkyEntity.createAttributes().build());
        event.put(ModEntities.MAGICAL_BOOK.get(), MagicalBookEntity.createAttributes().build());

        event.put(ModEntities.DUCK.get(), DuckEntity.createAttributes().build());
        event.put(ModEntities.CHARGED_SHEEP.get(), ChargedSheepEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerCharmAttributes(RegisterCharmAttributesEvent event) {
        event.register(
            CharmAttributes.Builder.of(Items.BUCKET)
            .addModifier(Attributes.ARMOR, getMod("Bucket Charm Armor", 2))
            .addType(CharmType.HEAD).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModTags.Items.SHULKER_SHELL)
            .addModifier(Attributes.ARMOR, getMod("Shulker Shell Charm Armor", 3))
            .addType(CharmType.HEAD).build()
        );
        event.register(
            CharmAttributes.Builder.of(Items.RABBIT_FOOT)
            .addModifier(Attributes.LUCK, getMod("Rabbit Foot Charm Luck", 1))
            .addType(CharmType.CHARM).build()
        );
        event.register(
            CharmAttributes.Builder.of(Items.TURTLE_HELMET)
            .addModifier(Attributes.ARMOR, getMod("Turtle Helmet Charm Armor", ((ArmorItem) Items.TURTLE_HELMET).getDefense()))
            .addType(CharmType.HEAD).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModItems.COMFY_SHOES)
            .addModifier(NeoForgeMod.STEP_HEIGHT.value(), ComfyShoesItem.getStepHeight())
            .addModifier(Attributes.MOVEMENT_SPEED, getMod("Comfy Shoes Charm Movement Speed", 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL))
            .addType(CharmType.FEET).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModItems.POWER_GAUNTLET)
            .addModifier(Attributes.ATTACK_DAMAGE, getMod("Robotic Glove Charm Attack Damage", 2))
            .addModifier(Attributes.ATTACK_SPEED, getMod("Robotic Glove Charm Attack Speed", 0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL))
            .addModifier(NeoForgeMod.BLOCK_REACH.value(), getMod("Robotic Glove Charm Block Reach", -1))
            .addType(CharmType.HAND).build()
        );
        event.register(
            CharmAttributes.Builder.of(Items.OBSERVER)
            .addModifier(NeoForgeMod.ENTITY_REACH.value(), getMod("Observer Charm Entity Reach", 1))
            .addModifier(NeoForgeMod.BLOCK_REACH.value(), getMod("Observer Charm Block Reach", 1))
            .addType(CharmType.HEAD).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModItems.HEART_NECKLACE)
            .addModifier(Attributes.MAX_HEALTH, getMod("Heart Necklace Charm Max Health", 4))
            .addType(CharmType.NECKLACE).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModItems.BAND_OF_ARCANE)
            .addModifier(ModAttributes.SPELL_POWER.get(), getMod("Band of Arcane Charm Spell Power", 5))
            .addModifier(ModAttributes.SPELL_COOLDOWN_REDUCTION.get(), getMod("Band of Arcane Charm Cooldown Reduction", 5))
            .addType(CharmType.RING).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModItems.SOLAR_PANEL_HELMET)
            .addModifier(Attributes.ARMOR, getMod("Solar Panel Charm Armor", ((ArmorItem) ModItems.SOLAR_PANEL_HELMET.get()).getDefense()))
            .addType(CharmType.HEAD).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModItems.IRON_LEATHER_GLOVES)
            .addModifier(Attributes.ARMOR, getMod("Iron Leather Gloves Armor", 3))
            .addType(CharmType.HAND).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModItems.DEATH_CAP)
            .addModifier(ModAttributes.SPELL_POWER.get(), getMod("Death Cap Spell Power", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL))
            .addType(CharmType.HEAD).build()
        );
        event.register(
            CharmAttributes.Builder.of(ModItems.MOONGLASS_PENDANT)
                .addModifier(ModAttributes.SPELL_POWER.get(), getMod("Moonglass Pendant Spell Power", 5))
                .addModifier(Attributes.ARMOR, getMod("Moonglass Pendant Armor", 1))
                .addModifier(Attributes.ARMOR_TOUGHNESS, getMod("Moonglass Pendant Armor Toughness", 1))
                .addType(CharmType.NECKLACE).build()
        );
    }

    private static AttributeModifier getMod(String name, double amount) {
        return getMod(name, amount, AttributeModifier.Operation.ADDITION);
    }
    private static AttributeModifier getMod(String name, double amount, AttributeModifier.Operation operation) {
        return new AttributeModifier(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name, amount, operation);
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.DUCK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.DEMON_EYE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
    }

    @SubscribeEvent
    public static void registerCauldronFluidContent(RegisterCauldronFluidContentEvent event) {
        event.register(ModBlocks.PLASTIC_CAULDRON.get(), ModFluids.LIQUID_PLASTIC.getFluid(), FluidType.BUCKET_VOLUME, null);
    }

    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.SPELL_POWER.get());
        event.add(EntityType.PLAYER, ModAttributes.SPELL_COOLDOWN_REDUCTION.get());
    }

    @SubscribeEvent
    public static void newRegistryEvent(NewRegistryEvent event) {
        event.register(ModRegistries.SPELL);
        event.register(ModRegistries.TAB_TYPE);
        event.register(ModRegistries.MULTIBLOCK);
    }
    
    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(ModItems.BLUEPRINT, new BlueprintDecorator());
    }
    
    @SubscribeEvent
    public static void registerTicketControllers(RegisterTicketControllersEvent event) {
        event.register(ForceLoadManager.TICKET_CONTROLLER);
    }
}