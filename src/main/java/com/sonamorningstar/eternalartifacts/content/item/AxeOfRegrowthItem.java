package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.block_search.TreeCapitatorHandler;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModTiers;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.MossBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AxeOfRegrowthItem extends AxeItem {
    public AxeOfRegrowthItem(Properties properties) {
        super(ModTiers.CHLOROPHYTE, 5.0F, -3.0F, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity target, LivingEntity pAttacker) {
        target.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 100, 0));
        return super.hurtEnemy(pStack, target, pAttacker);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        BlockPos pos = ctx.getClickedPos();
        ItemStack stack = ctx.getItemInHand();
        BlockState state = level.getBlockState(pos);
        if(state.getBlock() instanceof MossBlock mossBlock && mossBlock.isValidBonemealTarget(level, pos, state)) {
            if(mossBlock.isBonemealSuccess(level, level.random, pos, state)) {
                if(player instanceof ServerPlayer) CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, pos, stack);
                if(level instanceof ServerLevel sl) mossBlock.performBonemeal(sl, level.getRandom(), pos, state);
                if(player != null) stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(ctx.getHand()));
                level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 0);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useOn(ctx);
    }

    /*@Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if (living instanceof Player player &&
                !MINERS.contains(player) &&
                !level.isClientSide() &&
                !player.isShiftKeyDown() &&
                state.is(BlockTags.LOGS)) {
            MINERS.add(player);
            boolean ret = TreeCapitatorHandler.onBlockBreak(level, player, pos, stack);
            MINERS.remove(player);
            return ret;
        } else return super.mineBlock(stack, level, state, pos, living);
    }*/

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.BLOCK_FORTUNE) {
            int level = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
            int silkTouch = stack.getEnchantmentLevel(Enchantments.SILK_TOUCH);
            if (silkTouch > 0) return 0;
            else return Math.max(level, 3);
        }
        return super.getEnchantmentLevel(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        MutableComponent ench = Enchantments.BLOCK_FORTUNE.getFullname(3).copy();
        int silkTouchLevel = pStack.getEnchantmentLevel(Enchantments.SILK_TOUCH);
        if (silkTouchLevel <= 0)
            pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("item_ench_text"), ench));
    }
}