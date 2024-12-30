package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModEnchantments;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;

public class ChiselItem extends DiggerItem {
    public ChiselItem(Tier tier, Properties props) {
        super(1, -3.0F, tier, BlockTags.MINEABLE_WITH_PICKAXE, props);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        if (stack.getEnchantmentLevel(ModEnchantments.VERSATILITY.get()) > 0) {
            return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
        }else return super.canPerformAction(stack, toolAction);
    }
}
