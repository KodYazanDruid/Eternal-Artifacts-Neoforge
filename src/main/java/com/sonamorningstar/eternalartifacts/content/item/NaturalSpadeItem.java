package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTiers;
import net.minecraft.world.item.ShovelItem;

public class NaturalSpadeItem extends ShovelItem {
    public NaturalSpadeItem(Properties pProperties) {
        super(ModTiers.CHLOROPHYTE, 1.5f, -3.0f, pProperties);
    }
}
