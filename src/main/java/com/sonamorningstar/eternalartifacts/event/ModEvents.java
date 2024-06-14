package com.sonamorningstar.eternalartifacts.event;

import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.content.block.entity.SidedTransferBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.DemonEyeEntity;
import com.sonamorningstar.eternalartifacts.content.entity.DuckEntity;
import com.sonamorningstar.eternalartifacts.content.entity.PinkyEntity;
import com.sonamorningstar.eternalartifacts.content.item.BatteryItem;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.NOUS_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.LIQUID_MEAT_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.PINK_SLIME_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.BLOOD_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.LIQUID_PLASTIC_BUCKET.get());

        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> new ModItemEnergyStorage(10000, 250, stack), ModItems.BATTERY.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack(stack, 1000), ModItems.JAR.get());

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.RESONATOR.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.JAR.get(), (be, ctx) -> be.tank);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BIOFURNACE.get(), (be, context) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BIOFURNACE.get(), (be, context) -> be.inventory);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> regSidedItemCaps(be, be.inventory, ctx, List.of(1)));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> regSidedFluidCaps(be, be.tank, ctx));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MEAT_PACKER.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MEAT_PACKER.get(), (be, ctx) -> regSidedItemCaps(be, be.inventory, ctx, List.of(0)));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MEAT_PACKER.get(), (be, ctx) -> regSidedFluidCaps(be, be.tank, ctx));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MEAT_SHREDDER.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.MEAT_SHREDDER.get(), (be, ctx) -> regSidedItemCaps(be, be.inventory, ctx, null));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MEAT_SHREDDER.get(), (be, ctx) -> regSidedFluidCaps(be, be.tank, ctx));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BATTERY_BOX.get(), (be, ctx) -> regSidedEnergyCaps(be, be.energy, ctx));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BATTERY_BOX.get(), (be, ctx) -> regSidedItemCaps(be, be.inventory, ctx, null));

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.MOB_LIQUIFIER.get(), (be, ctx) -> regSidedEnergyCaps(be, be.energy, ctx));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.MOB_LIQUIFIER.get(), (be, ctx) -> regSidedFluidCaps(be, be.tanks, ctx));


    }

    private static IItemHandlerModifiable regSidedItemCaps(SidedTransferBlockEntity<?> be, IItemHandlerModifiable inventory, Direction ctx, @Nullable List<Integer> outputSlots) {
        if (ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferBlockEntity.canPerformTransfer(be, ctx, SidedTransferBlockEntity.TransferType.NONE) || !be.isItemsAllowed()) return null;
            return new WrappedItemStorage(inventory,
                    i -> (outputSlots != null && outputSlots.contains(i)) &&
                            SidedTransferBlockEntity.canPerformTransfers(be, ctx, SidedTransferBlockEntity.TransferType.PUSH, SidedTransferBlockEntity.TransferType.DEFAULT) &&
                            be.isItemsAllowed(),
                    (i, s) -> (outputSlots == null || !outputSlots.contains(i)) &&
                            SidedTransferBlockEntity.canPerformTransfers(be ,ctx, SidedTransferBlockEntity.TransferType.PULL, SidedTransferBlockEntity.TransferType.DEFAULT) &&
                            be.isItemsAllowed());
        } else return inventory;
    }

    private static IFluidHandler regSidedFluidCaps(SidedTransferBlockEntity<?> be, IFluidHandler tank, Direction ctx) {
        if(ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferBlockEntity.canPerformTransfer(be, ctx, SidedTransferBlockEntity.TransferType.NONE) || !be.isFluidsAllowed()) return null;
            return new WrappedFluidStorage(tank,
                    dir -> SidedTransferBlockEntity.canPerformTransfers(be, dir, SidedTransferBlockEntity.TransferType.PUSH, SidedTransferBlockEntity.TransferType.DEFAULT) &&
                            be.isFluidsAllowed(),
                    (dir, fs) -> SidedTransferBlockEntity.canPerformTransfers(be, dir, SidedTransferBlockEntity.TransferType.PULL, SidedTransferBlockEntity.TransferType.DEFAULT) &&
                            be.isFluidsAllowed(),
                    ctx);
        } else return tank;
    }

    private static IEnergyStorage regSidedEnergyCaps(SidedTransferBlockEntity<?> be, IEnergyStorage energy, Direction ctx) {
        if(ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferBlockEntity.canPerformTransfer(be, ctx, SidedTransferBlockEntity.TransferType.NONE)) return null;
            return new WrappedEnergyStorage(energy,
                    dir -> SidedTransferBlockEntity.canPerformTransfers(be, dir, SidedTransferBlockEntity.TransferType.PUSH, SidedTransferBlockEntity.TransferType.DEFAULT),
                    dir -> SidedTransferBlockEntity.canPerformTransfers(be, dir, SidedTransferBlockEntity.TransferType.PULL, SidedTransferBlockEntity.TransferType.DEFAULT),
                    ctx);
        }else return energy;
    }


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DEMON_EYE.get(), DemonEyeEntity.createAttributes().build());
        event.put(ModEntities.PINKY.get(), PinkyEntity.createAttributes().build());
        event.put(ModEntities.DUCK.get(), DuckEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.DUCK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
    }

}