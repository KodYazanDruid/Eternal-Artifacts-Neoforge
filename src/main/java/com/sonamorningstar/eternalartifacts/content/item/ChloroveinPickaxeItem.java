package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTiers;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChloroveinPickaxeItem extends PickaxeItem {
    public ChloroveinPickaxeItem(Properties properties) {
        super(ModTiers.CHLOROPHYTE, 1, -2.8f, properties);
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        int level = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
        return enchantment == Enchantments.BLOCK_FORTUNE ? Math.max(level, 1) : 0;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        MutableComponent ench = Enchantments.BLOCK_FORTUNE.getFullname(1).copy();
        pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("item_ench_text"), ench));
    }
}
