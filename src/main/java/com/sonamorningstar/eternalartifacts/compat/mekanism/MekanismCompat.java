package com.sonamorningstar.eternalartifacts.compat.mekanism;

import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.event.custom.JarDrinkEvent;
import com.sonamorningstar.eternalartifacts.compat.ModHooks;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.registries.MekanismItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import mekanism.common.tags.MekanismTags;
import mekanism.generators.common.GeneratorTags;

import java.util.function.Supplier;

public class MekanismCompat {

    public static void drinkEventMekanism(JarDrinkEvent event) {
        FluidStack fluidStack = event.getFluidStack();
        if(fluidStack.is(MekanismTags.Fluids.URANIUM_OXIDE)) {
            event.setDefaultUseTime();
            event.setAfterDrink((player, itemStack) -> {
                MobEffectInstance effect = new MobEffectInstance(MobEffects.POISON, 600, 4);
                player.addEffect(effect);
            });
        }
    }

    public static void drinkEventMekanismGenerators(JarDrinkEvent event) {
        FluidStack fluidStack = event.getFluidStack();
        if (fluidStack.is(GeneratorTags.Fluids.FUSION_FUEL)) {
            event.setDefaultUseTime();
            event.setAfterDrink((player, itemStack) -> {
                player.level().explode(null, player.getX(), player.getY(), player.getZ(), 10.0F, true, Level.ExplosionInteraction.BLOCK);
            });
        }
    }
    
    private static void registerMekCaps(RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.GAS.block(), (a, b, c, d, e) -> new IGasHandler() {
			@Override
			public int getTanks() {
				return 1;
			}
			
			@Override
			public GasStack getChemicalInTank(int i) {
				return GasStack.EMPTY;
			}
			
			@Override
			public void setChemicalInTank(int i, GasStack stack) {
			
			}
			
			@Override
			public long getTankCapacity(int i) {
				return 16000;
			}
			
			@Override
			public boolean isValid(int i, GasStack stack) {
				return true;
			}
			
			@Override
			public GasStack insertChemical(int i, GasStack stack, Action action) {
				return GasStack.EMPTY;
			}
			
			@Override
			public GasStack extractChemical(int i, long l, Action action) {
				return GasStack.EMPTY;
			}
		}, ModBlocks.TRASH_CAN.get());
    }


    public static void runMek(IEventBus modEventBus) {
        addTag(ModTags.Items.CHARMS_BACK, MekanismItems.HDPE_REINFORCED_ELYTRA::get);
        modEventBus.addListener(RegisterCapabilitiesEvent.class, MekanismCompat::registerMekCaps);
    }

    public static void runMekGens(IEventBus modEventBus) {

    }

    private static void addTag(TagKey<Item> tagKey, Supplier<Item> item) {
        ModHooks.ItemTagAppender.appendTag(tagKey, item);
    }
}
