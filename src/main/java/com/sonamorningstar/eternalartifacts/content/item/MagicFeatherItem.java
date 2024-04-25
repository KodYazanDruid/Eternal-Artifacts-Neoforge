package com.sonamorningstar.eternalartifacts.content.item;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagicFeatherItem extends ArtifactItem{
    public static Pair<Boolean, Integer> activeTicks = Pair.of(false, 0);
    public MagicFeatherItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return super.isFoil(pStack) || (activeTicks != null ? activeTicks.getFirst() : false);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (activeTicks != null && activeTicks.getFirst()) pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("magic_feather_active")).withStyle(ChatFormatting.GREEN));
        else pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("magic_feather_not_active")).withStyle(ChatFormatting.GRAY));
    }
}
