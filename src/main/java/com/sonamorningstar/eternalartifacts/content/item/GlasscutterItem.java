package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;

public class GlasscutterItem extends DiggerItem {
    public GlasscutterItem(Properties pProperties) {
        super(1.0F, -2.0F, Tiers.IRON, ModTags.Blocks.MINEABLE_WITH_GLASSCUTTER, pProperties);
    }

    /*@Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if(enchantment == Enchantments.SILK_TOUCH) return 1;
        return super.getEnchantmentLevel(stack, enchantment);
    }*/

}
