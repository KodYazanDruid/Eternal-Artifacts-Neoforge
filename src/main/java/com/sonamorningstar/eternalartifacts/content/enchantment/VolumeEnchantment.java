package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.content.item.*;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class VolumeEnchantment extends Enchantment {
    public static final List<Class<?>> acceptedItems = List.of(
            FeedingCanister.class,
            EnderNotebookItem.class
    );

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
