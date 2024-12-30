package com.sonamorningstar.eternalartifacts.content.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class HammerItem extends DiggerItem {
    public HammerItem(Tier tier, Properties props) {
        super(6.0F, -3.2F, tier, BlockTags.MINEABLE_WITH_PICKAXE, props);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack copy = itemStack.copy();
        if (copy.isDamageableItem()) {
            copy.setDamageValue(copy.getDamageValue() + 1);
            if (copy.getDamageValue() >= copy.getMaxDamage()) {
                return ItemStack.EMPTY;
            }
        }
        /*FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(null);
        itemStack.hurtAndBreak(1 , fakePlayer, pl -> {});*/
        return copy;
    }
}
