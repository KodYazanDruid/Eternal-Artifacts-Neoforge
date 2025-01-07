package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.enchantment.VersatilityEnchantment;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;

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
