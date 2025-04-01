package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.enchantment.VersatilityEnchantment;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;

public class HammaxeItem extends DiggerItem {
    public HammaxeItem(Tier tier, Properties props) {
        super(6.0F, -3.2F, tier, ModTags.Blocks.MINEABLE_WITH_HAMMAXE, props);
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
    public boolean hurtEnemy(ItemStack pStack, LivingEntity target, LivingEntity pAttacker) {
        target.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 100, 0));
        return super.hurtEnemy(pStack, target, pAttacker);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        BlockState newState = state.getToolModifiedState(context, ToolActions.AXE_SCRAPE, false);
        if (newState != null) {
            level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, pos, 0);
        } else {
            newState = state.getToolModifiedState(context, ToolActions.AXE_WAX_OFF, false);
            if (newState != null) {
                level.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos, 0);
            } else {
                newState = state.getToolModifiedState(context, ToolActions.AXE_STRIP, false);
                if (newState != null) {
                    level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }

        if (newState != null) {
            level.setBlockAndUpdate(pos, newState);
            if (player != null) stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        if (VersatilityEnchantment.has(stack)) {
            return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) ||
                    ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
        }
        return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction);
    }
}
