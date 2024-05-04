package com.sonamorningstar.eternalartifacts.event;

import com.sonamorningstar.eternalartifacts.capabilities.WrappedModItemStorage;
import com.sonamorningstar.eternalartifacts.content.entity.DemonEyeEntity;
import com.sonamorningstar.eternalartifacts.content.entity.PinkyEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import com.sonamorningstar.eternalartifacts.core.ModEntities;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidBucketWrapper(stack), ModItems.NOUS_BUCKET.get());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.RESONATOR.get(), (be, ctx) -> be.energy);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BIOFURNACE.get(), (be, context) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BIOFURNACE.get(), (be, context) -> be.inventory);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> be.energy);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> {
            if (ctx != null) return new WrappedModItemStorage(be.inventory, i -> i == 1, (i, s) -> i != 1);
            else return be.inventory;
        });
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.BOOK_DUPLICATOR.get(), (be, ctx) -> be.tank);

    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DEMON_EYE.get(), DemonEyeEntity.createAttributes().build());
        event.put(ModEntities.PINKY.get(), PinkyEntity.createAttributes().build());
    }


}