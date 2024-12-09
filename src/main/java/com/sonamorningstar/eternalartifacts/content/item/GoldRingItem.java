package com.sonamorningstar.eternalartifacts.content.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GoldRingItem extends Item {
    public GoldRingItem(Properties props) {
        super(props);
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }
}
