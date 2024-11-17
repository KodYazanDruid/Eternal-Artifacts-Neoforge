package com.sonamorningstar.eternalartifacts.content.item.base;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VolumeHolderItem extends Item {
    public VolumeHolderItem(Properties props) {
        super(props);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 18;
    }
}
