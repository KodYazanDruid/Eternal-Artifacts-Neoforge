package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnchantedGoldenAncientFruitItem extends Item  {
    public EnchantedGoldenAncientFruitItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("enchanted_golden_ancient_fruit")));
    }
}
