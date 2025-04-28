package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.content.item.block.JarBlockItem;
import com.sonamorningstar.eternalartifacts.content.item.block.TigrisFlowerItem;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Set;
import java.util.function.Predicate;

public class VolumeEnchantment extends Enchantment {
    private static final Set<Class<?>> acceptedItems = Set.of(
        FeedingCanister.class,
        EnderNotebookItem.class,
        ChlorophyteRepeaterItem.class
    );
    private static final Set<Class<?>> blacklistedItems = Set.of(
        BucketItem.class,
        TigrisFlowerItem.class,
        JarBlockItem.class
    );

    public static Predicate<ItemStack> isAcceptedItem = stack -> {
        Class<?> clazz = stack.getItem().getClass();
        return acceptedItems.contains(clazz) || acceptedItems.stream().anyMatch(clazz::isInstance);
    };

    public static Predicate<ItemStack> isBlacklistedItem = stack -> {
        Class<?> clazz = stack.getItem().getClass();
        return blacklistedItems.contains(clazz) || blacklistedItems.stream().anyMatch(clazz::isInstance);
    };

    public VolumeEnchantment() {
        super(Rarity.UNCOMMON, ModEnchantments.ModEnchantmentCategory.VOLUME, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int pLevel) {
        return super.getMinCost(pLevel);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

}
