package com.sonamorningstar.eternalartifacts.content.item.block;

import com.sonamorningstar.eternalartifacts.content.item.block.base.RetexturedBlockItem;
import com.sonamorningstar.eternalartifacts.core.ModBlocks;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GardeningPotBlockItem extends RetexturedBlockItem {
    public GardeningPotBlockItem(TagKey<Item> itemTagKey, Properties builder) {
        super(ModBlocks.GARDENING_POT.get(), itemTagKey, builder);
    }

    @Override
    public ItemStack getDefaultInstance() {
        CompoundTag tag = new CompoundTag();
        tag.putString(RetexturedHelper.TEXTURE_TAG_KEY, RetexturedHelper.getTextureName(Blocks.TERRACOTTA));
        return new ItemStack(this, 1, tag);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.ofItem(ModItems.GARDENING_POT)).withStyle(ChatFormatting.GRAY));
    }
}
