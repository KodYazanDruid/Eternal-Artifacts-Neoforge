package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;

public class GlasscutterItem extends DiggerItem {
    public GlasscutterItem(Tier tier, Properties pProperties) {
        super(1.0F, -2.0F, tier, ModTags.Blocks.MINEABLE_WITH_GLASSCUTTER, pProperties);
    }

    /*@Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if(enchantment == Enchantments.SILK_TOUCH) return 1;
        return super.getEnchantmentLevel(stack, enchantment);
    }*/

}
