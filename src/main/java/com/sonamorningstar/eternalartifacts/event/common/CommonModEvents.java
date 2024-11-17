package com.sonamorningstar.eternalartifacts.event.common;

import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronDrainInteraction;
import com.sonamorningstar.eternalartifacts.api.cauldron.ModCauldronInteraction;
import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.capabilities.energy.ModItemEnergyStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.InfiniteWaterTank;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModItemMultiFluidTank;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModScaleableItemItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.DrumBlock;
import com.sonamorningstar.eternalartifacts.content.block.FancyChestBlock;
import com.sonamorningstar.eternalartifacts.content.entity.DemonEyeEntity;
import com.sonamorningstar.eternalartifacts.content.entity.DuckEntity;
import com.sonamorningstar.eternalartifacts.content.entity.MagicalBookEntity;
import com.sonamorningstar.eternalartifacts.content.entity.PinkyEntity;
import com.sonamorningstar.eternalartifacts.core.*;
import com.sonamorningstar.eternalartifacts.registrar.ModRegistries;
import com.sonamorningstar.eternalartifacts.util.CapabilityHelper;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.ArrayList;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        ModFluids.FLUIDS.forEachBucketEntry(holder -> event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), holder.get()));

        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> {
            int volumeLevel = stack.getEnchantmentLevel(ModEnchantments.VOLUME.get());
            int capacity = (volumeLevel + 1) * 50000;
            int transfer = (volumeLevel + 1) * 2500;
            return new ModItemEnergyStorage(capacity, transfer, stack);
        }, ModItems.BATTERY.get());

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

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.RESONATOR.get(), (be, ctx) ->
                be.getBlockState().getValue(BlockStateProperties.FACING) == ctx ? be.energy : null);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.JAR.get(), (be, ctx) -> be.tank);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BIOFURNACE.get(), (be, context) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BIOFURNACE.get(), (be, context) -> be.inventory);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> CapabilityHelper.regSidedItemCaps(be, be.inventory, ctx, List.of(1)));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> CapabilityHelper.regSidedFluidCaps(be, be.tank, ctx));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BATTERY_BOX.get(), (be, ctx) -> CapabilityHelper.regSidedEnergyCaps(be, be.energy, ctx));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BATTERY_BOX.get(), (be, ctx) -> CapabilityHelper.regSidedItemCaps(be, be.inventory, ctx, null));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), (be, ctx) ->
                be.getBlockState().getValue(BlockStateProperties.FACING) == ctx ? be.energy : null);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), (be, ctx) -> be.tank);

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
    }

    private static void registerDrum(RegisterCapabilitiesEvent event, DeferredBlock<DrumBlock> holder) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack(stack, holder.get().getCapacity()), holder.asItem());
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DEMON_EYE.get(), DemonEyeEntity.createAttributes().build());
        event.put(ModEntities.PINKY.get(), PinkyEntity.createAttributes().build());
        event.put(ModEntities.MAGICAL_BOOK.get(), MagicalBookEntity.createAttributes().build());

        event.put(ModEntities.DUCK.get(), DuckEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.DUCK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
    }

    @SubscribeEvent
    public static void registerCauldronFluidContent(RegisterCauldronFluidContentEvent event) {
        event.register(ModBlocks.PLASTIC_CAULDRON.get(), ModFluids.LIQUID_PLASTIC.getFluid(), FluidType.BUCKET_VOLUME, null);
    }

    @SubscribeEvent
    public static void addAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.SPELL_DAMAGE.get());
        event.add(EntityType.PLAYER, ModAttributes.COOLDOWN_REDUCTION.get());
    }

    @SubscribeEvent
    public static void newRegistryEvent(NewRegistryEvent event) {
        event.register(ModRegistries.SPELL);
        event.register(ModRegistries.TAB_TYPE);
    }

}