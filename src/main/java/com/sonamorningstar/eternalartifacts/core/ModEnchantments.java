package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.enchantment.MeltingTouchEnchantment;
import com.sonamorningstar.eternalartifacts.content.enchantment.SoulboundEnchantment;
import com.sonamorningstar.eternalartifacts.content.enchantment.VersatilityEnchantment;
import com.sonamorningstar.eternalartifacts.content.enchantment.VolumeEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, MODID);

    public static final DeferredHolder<Enchantment, VolumeEnchantment> VOLUME = ENCHANTMENTS.register("volume", VolumeEnchantment::new);
    public static final DeferredHolder<Enchantment, VersatilityEnchantment> VERSATILITY = ENCHANTMENTS.register("versatility", VersatilityEnchantment::new);
    public static final DeferredHolder<Enchantment, SoulboundEnchantment> SOULBOUND = ENCHANTMENTS.register("soulbound", SoulboundEnchantment::new);
    public static final DeferredHolder<Enchantment, MeltingTouchEnchantment> MELTING_TOUCH = ENCHANTMENTS.register("melting_touch", MeltingTouchEnchantment::new);

    public static class ModEnchantmentCategory{
        public static final EnchantmentCategory VOLUME = EnchantmentCategory.create("volume", item -> {
            ItemStack stack = item.getDefaultInstance();
            return ((hasAnyCapability(stack) && BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(MODID))
                || VolumeEnchantment.isAcceptedItem.test(stack)) && !VolumeEnchantment.isBlacklistedItem.test(stack);
        });
        public static final EnchantmentCategory VERSATILITY = EnchantmentCategory.create("versatility", VersatilityEnchantment.acceptedItems);
        public static final EnchantmentCategory SOULBOUND = EnchantmentCategory.create("soulbound", item -> item.getMaxStackSize(item.getDefaultInstance()) == 1);

        private static boolean hasAnyCapability(ItemStack stack) {
            return hasEnergy(stack) || hasInventory(stack) || hasTank(stack);
        }
        private static boolean hasEnergy(ItemStack stack) {
            return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
        }
        private static boolean hasInventory(ItemStack stack) {
            return stack.getCapability(Capabilities.ItemHandler.ITEM) != null;
        }
        private static boolean hasTank(ItemStack stack) {
            return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
        }
    }
}
