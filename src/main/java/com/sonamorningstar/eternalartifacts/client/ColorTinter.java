package com.sonamorningstar.eternalartifacts.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ColorTinter {

    public static int getColorFromNBT(ItemStack stack, int tintIndex) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null) {
            int color = nbt.getInt("Color");
            return color != 0 ? nbt.getInt("Color") : 0xFFFFFFFF;
        }
        return 0xFFFFFFFF;
    }
}
