package com.sonamorningstar.eternalartifacts.content.item;

import com.mojang.datafixers.util.Pair;
import com.sonamorningstar.eternalartifacts.content.enchantment.VersatilityEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class HammerItem extends DiggerItem {
    public static final Set<TagKey<Block>> gatheredTags = new HashSet<>();
    public static final Set<Block> gatheredBlocks = new HashSet<>();
    public static final Map<TagKey<Block>, Pair<Item, Pair<Float, Float>>> tagDropRates = new ConcurrentHashMap<>();
    public static final Map<Block, Pair<Item, Pair<Float, Float>>> blockDropRates = new ConcurrentHashMap<>();
    
    public static ResourceLocation getTableForTag(TagKey<Block> tag) {
        ResourceLocation loc = tag.location();
        return new ResourceLocation(MODID, "hammering/tags/" + loc.getNamespace() + "/" + loc.getPath());
    }
    
    public static ResourceLocation getTableForBlock(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return new ResourceLocation(MODID, "hammering/blocks/" + id.getNamespace() + "/" + id.getPath());
    }
    
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
        return copy;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        if (VersatilityEnchantment.has(stack)) {
            return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
        }
        return super.canPerformAction(stack, toolAction);
    }
}
