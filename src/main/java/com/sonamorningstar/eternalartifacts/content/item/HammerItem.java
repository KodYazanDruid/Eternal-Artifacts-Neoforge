package com.sonamorningstar.eternalartifacts.content.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;

public class HammerItem extends DiggerItem {
    public HammerItem(Tier tier, Properties props) {
        super(6.0F, -3.2F, tier, BlockTags.MINEABLE_WITH_PICKAXE, props);
    }
}
