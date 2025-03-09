package com.sonamorningstar.eternalartifacts.content.enchantment;

import com.sonamorningstar.eternalartifacts.content.item.ChiselItem;
import com.sonamorningstar.eternalartifacts.content.item.CutlassItem;
import com.sonamorningstar.eternalartifacts.content.item.HammaxeItem;
import com.sonamorningstar.eternalartifacts.content.item.HammerItem;
import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.function.Predicate;

public class VersatilityEnchantment extends Enchantment {
    private static final List<Class<?>> acceptedClasses = List.of(
        PickaxeItem.class,
        ShovelItem.class,
        AxeItem.class,
        ChiselItem.class,
        HammerItem.class,
        HammaxeItem.class,
        CutlassItem.class
    );
    public static final Predicate<Item> acceptedItems = item -> {
        for (Class<?> aClass : acceptedClasses) {
            if (aClass.isAssignableFrom(item.getClass())) {
                return true;
            }
        }
        return false;
    };

    public VersatilityEnchantment() {
        super(Rarity.VERY_RARE, ModEnchantments.ModEnchantmentCategory.VERSATILITY, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public static boolean has(ItemStack stack) {
        return stack.getEnchantmentLevel(ModEnchantments.VERSATILITY.get()) > 0;
    }

    @Override
    public int getMinCost(int pEnchantmentLevel) {
        return 30;
    }
    @Override
    public int getMaxCost(int pEnchantmentLevel) {
        return 50;
    }

    @Override
    public boolean isTreasureOnly() {return true;}
    @Override
    public boolean isTradeable() {return false;}
}
