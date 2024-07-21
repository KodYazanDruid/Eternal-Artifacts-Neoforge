package com.sonamorningstar.eternalartifacts.event;

import com.sonamorningstar.eternalartifacts.capabilities.*;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.SidedTransferMachineBlockEntity;
import com.sonamorningstar.eternalartifacts.content.entity.DemonEyeEntity;
import com.sonamorningstar.eternalartifacts.content.entity.DuckEntity;
import com.sonamorningstar.eternalartifacts.content.entity.MagicalBookEntity;
import com.sonamorningstar.eternalartifacts.content.entity.PinkyEntity;
import com.sonamorningstar.eternalartifacts.content.item.block.base.FluidHolderBlockItem;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;
import static com.sonamorningstar.eternalartifacts.content.item.block.base.FluidHolderBlockItem.ModCauldronDrainInteraction.PLASTIC;
import static com.sonamorningstar.eternalartifacts.content.item.block.base.FluidHolderBlockItem.ModCauldronInteraction.EMPTY;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.NOUS_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.LIQUID_MEAT_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.PINK_SLIME_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.BLOOD_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.LIQUID_PLASTIC_BUCKET.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.BEER_BUCKET.get());

        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> new ModItemEnergyStorage(10000, 250, stack), ModItems.BATTERY.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack(stack, 1000), ModItems.JAR.get());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack(stack, 64000), ModBlocks.NOUS_TANK.asItem());
        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new ModItemItemStorage(stack, 27), ModItems.KNAPSACK.get());

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

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.FLUID_COMBUSTION_DYNAMO.get(), (be, ctx) -> be.tank);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.NOUS_TANK.get(), (be, ctx) -> regSidedFluidCaps(be, be.tank, ctx));

    }

    @Contract("_, _, null, _ -> param2")
    private static @org.jetbrains.annotations.Nullable IItemHandlerModifiable regSidedItemCaps(SidedTransferMachineBlockEntity<?> be, IItemHandlerModifiable inventory, Direction ctx, @Nullable List<Integer> outputSlots) {
        if (ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferMachineBlockEntity.canPerformTransfer(be, ctx, SidedTransferMachineBlockEntity.TransferType.NONE) || !be.isItemsAllowed()) return null;
            return new WrappedItemStorage(inventory,
                    i -> (outputSlots != null && outputSlots.contains(i)) &&
                            SidedTransferMachineBlockEntity.canPerformTransfers(be, ctx, SidedTransferMachineBlockEntity.TransferType.PUSH, SidedTransferMachineBlockEntity.TransferType.DEFAULT) &&
                            be.isItemsAllowed(),
                    (i, s) -> (outputSlots == null || !outputSlots.contains(i)) &&
                            SidedTransferMachineBlockEntity.canPerformTransfers(be ,ctx, SidedTransferMachineBlockEntity.TransferType.PULL, SidedTransferMachineBlockEntity.TransferType.DEFAULT) &&
                            be.isItemsAllowed());
        } else return inventory;
    }

    @Contract("_, _, null -> param2")
    private static @org.jetbrains.annotations.Nullable IFluidHandler regSidedFluidCaps(SidedTransferMachineBlockEntity<?> be, IFluidHandler tank, Direction ctx) {
        if(ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferMachineBlockEntity.canPerformTransfer(be, ctx, SidedTransferMachineBlockEntity.TransferType.NONE) || !be.isFluidsAllowed()) return null;
            return new WrappedFluidStorage(tank,
                    dir -> SidedTransferMachineBlockEntity.canPerformTransfers(be, dir, SidedTransferMachineBlockEntity.TransferType.PUSH, SidedTransferMachineBlockEntity.TransferType.DEFAULT) &&
                            be.isFluidsAllowed(),
                    (dir, fs) -> SidedTransferMachineBlockEntity.canPerformTransfers(be, dir, SidedTransferMachineBlockEntity.TransferType.PULL, SidedTransferMachineBlockEntity.TransferType.DEFAULT) &&
                            be.isFluidsAllowed(),
                    ctx);
        } else return tank;
    }

    @Contract("_, _, null -> param2")
    private static @org.jetbrains.annotations.Nullable IEnergyStorage regSidedEnergyCaps(SidedTransferMachineBlockEntity<?> be, IEnergyStorage energy, Direction ctx) {
        if(ctx != null) {
            be.invalidateCapabilities();
            if(SidedTransferMachineBlockEntity.canPerformTransfer(be, ctx, SidedTransferMachineBlockEntity.TransferType.NONE)) return null;
            return new WrappedEnergyStorage(energy,
                    dir -> SidedTransferMachineBlockEntity.canPerformTransfers(be, dir, SidedTransferMachineBlockEntity.TransferType.PUSH, SidedTransferMachineBlockEntity.TransferType.DEFAULT),
                    dir -> SidedTransferMachineBlockEntity.canPerformTransfers(be, dir, SidedTransferMachineBlockEntity.TransferType.PULL, SidedTransferMachineBlockEntity.TransferType.DEFAULT),
                    ctx);
        }else return energy;
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
        event.register(ModBlocks.PLASTIC_CAULDRON.get(), ModFluids.LIQUID_PLASTIC.get().getSource(), FluidType.BUCKET_VOLUME, null);
    }

    @SubscribeEvent
    public static void fmlCommonSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(()-> {
            registerCauldronContextsForItemFluidHandlers(ModItems.JAR);
        });
    }

    private static void registerCauldronContextsForItemFluidHandlers(DeferredItem<?>... holders) {
        for(DeferredItem<?> holder : holders) {
            Item item = holder.get();
            CauldronInteraction.EMPTY.map().put(item, FluidHolderBlockItem.ModCauldronInteraction.EMPTY);
            CauldronInteraction.WATER.map().put(item, FluidHolderBlockItem.ModCauldronDrainInteraction.WATER);
            CauldronInteraction.LAVA.map().put(item, FluidHolderBlockItem.ModCauldronDrainInteraction.LAVA);
            FluidHolderBlockItem.ModCauldronInteraction.PLASTIC.map().put(item, PLASTIC);
        }
        FluidHolderBlockItem.ModCauldronInteraction.PLASTIC.map().put(Items.BUCKET, PLASTIC);
        CauldronInteraction.EMPTY.map().put(ModItems.LIQUID_PLASTIC_BUCKET.get(), EMPTY);
    }

}