package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;

public class GrafterItem extends DiggerItem {
    public GrafterItem(Tier tier, Properties props) {
        super(1.5F, -2.5F, tier, ModTags.Blocks.MINEABLE_WITH_GRAFTER, props.durability(32));
    }
}
