package com.sonamorningstar.eternalartifacts.core;

import com.sonamorningstar.eternalartifacts.content.enchantment.VersatilityEnchantment;
import com.sonamorningstar.eternalartifacts.content.enchantment.VolumeEnchantment;
import com.sonamorningstar.eternalartifacts.content.item.base.VolumeHolderItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, MODID);

    public static final DeferredHolder<Enchantment, Enchantment> VOLUME = ENCHANTMENTS.register("volume", VolumeEnchantment::new);
    public static final DeferredHolder<Enchantment, Enchantment> VERSATILITY = ENCHANTMENTS.register("versatility", VersatilityEnchantment::new);
    //generate a autosmelting enchantment
    //public static final DeferredHolder<Enchantment, Enchantment> AUTOSMELTING = ENCHANTMENTS.register("autosmelting", AutosmeltingEnchantment::new);


    public static class ModEnchantmentCategory{
        public static final EnchantmentCategory VOLUME_HOLDER = EnchantmentCategory.create("volume_holder", item -> item instanceof VolumeHolderItem);
        public static final EnchantmentCategory VERSATILITY = EnchantmentCategory.create("pickaxe", item -> VersatilityEnchantment.acceptedItems.contains(item.getClass()));
    }
}
