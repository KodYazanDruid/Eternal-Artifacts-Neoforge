package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import com.sonamorningstar.eternalartifacts.core.ModTiers;
import net.minecraft.world.item.DiggerItem;

public class GrafterItem extends DiggerItem {
    public GrafterItem(Properties props) {
        super(1.5F, -3.0F, ModTiers.COPPER, ModTags.Blocks.MINEABLE_WITH_GRAFTER, props);
    }
}
