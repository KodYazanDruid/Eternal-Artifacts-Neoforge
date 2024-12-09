package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModArmorMaterials;
import net.minecraft.world.item.ArmorItem;

public class ShulkerArmorItem extends ArmorItem {
    public ShulkerArmorItem(Type type, Properties props) {
        super(ModArmorMaterials.SHULKER, type, props);
    }
}
