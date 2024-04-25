package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.content.item.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FancyChestBlockItem extends RetexturedBlockItem {
    public FancyChestBlockItem(TagKey<Item> itemTagKey, Properties builder) {
        super(ModBlocks.FANCY_CHEST.get(), itemTagKey, builder);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        //pTooltip.add(Component.translatable("key." + MODID + ".gardening_pot_item.desc"));
    }
}
