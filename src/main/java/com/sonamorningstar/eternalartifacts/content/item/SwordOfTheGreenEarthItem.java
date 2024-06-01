package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTiers;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class SwordOfTheGreenEarthItem extends SwordItem {
    public SwordOfTheGreenEarthItem(Properties pProperties) {
        super(ModTiers.CHLOROPHYTE, 3, -2.4f, pProperties);
    }
}
