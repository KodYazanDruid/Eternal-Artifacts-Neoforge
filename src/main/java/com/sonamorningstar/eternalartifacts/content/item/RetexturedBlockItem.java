package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.util.RetexturedHelper;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class RetexturedBlockItem extends BlockItem {
    protected final TagKey<Item> itemTagKey;
    private final Block block;
    public RetexturedBlockItem(Block block, TagKey<Item> itemTagKey, Properties builder) {
        super(block, builder);
        this.itemTagKey = itemTagKey;
        this.block = block;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        addTooltip(pStack, pTooltip);
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    public static String getTextureName(ItemStack stack) {
        return RetexturedHelper.getTextureName(stack.getTag());
    }

    public static Block getTexture(ItemStack stack) {
        return RetexturedHelper.getBlock(getTextureName(stack));
    }

    public static void addTooltip(ItemStack pStack, List<Component> pTooltip) {
        Block block = getTexture(pStack);
        if(block != Blocks.AIR) pTooltip.add(Component.translatable(block.getDescriptionId()).withStyle(ChatFormatting.GRAY));
    }

    public static ItemStack setTexture(ItemStack stack, String name) {
        if(!name.isEmpty()) RetexturedHelper.setTexture(stack.getOrCreateTag(), name);
        else if(stack.hasTag()) RetexturedHelper.setTexture(stack.getTag(), name);
        return stack;
    }

    public static ItemStack setTexture(ItemStack stack, @Nullable Block block) {
        if(block == null || block == Blocks.AIR) return setTexture(stack, "");
        return setTexture(stack, BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    public void fillItemCategory(CreativeModeTab.Output output) {
        NonNullList<ItemStack> items = NonNullList.create();
        addTagVariants(getBlock(), itemTagKey, items, true);
        for(ItemStack stack : items) {
            output.accept(stack);
        }
    }

    public static void addTagVariants(ItemLike block, TagKey<Item> tag, NonNullList<ItemStack> list, boolean showAllVariants) {
        boolean added = false;
        Class<?> blockClass = block.getClass();
        for(Holder<Item> candidate : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            if(!candidate.isBound()) continue;

            Item item = candidate.value();
            if(!(item instanceof BlockItem)) continue;

            Block textureBlock = ((BlockItem)item).getBlock();
            if(blockClass.isInstance(textureBlock)) continue;

            added = true;
            list.add(setTexture(new ItemStack(block), textureBlock));
            if(!showAllVariants) return;
        }
        if(!added) list.add(new ItemStack(block));
    }
}
