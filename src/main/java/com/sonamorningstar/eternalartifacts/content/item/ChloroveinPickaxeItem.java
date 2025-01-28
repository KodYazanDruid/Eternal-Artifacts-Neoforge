package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.api.caches.BlockVeinCache;
import com.sonamorningstar.eternalartifacts.core.ModEffects;
import com.sonamorningstar.eternalartifacts.core.ModTiers;
import com.sonamorningstar.eternalartifacts.util.BlockHelper;
import com.sonamorningstar.eternalartifacts.util.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChloroveinPickaxeItem extends PickaxeItem {
    public ChloroveinPickaxeItem(Properties properties) {
        super(ModTiers.CHLOROPHYTE, 1, -2.8f, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity target, LivingEntity pAttacker) {
        target.addEffect(new MobEffectInstance(ModEffects.MALADY.get(), 100, 0));
        return super.hurtEnemy(pStack, target, pAttacker);
    }

    private static final List<ItemStack> currentMiners = new ArrayList<>();

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if (BlockHelper.isOre(level, pos) &&
                living instanceof Player player &&
                !living.isShiftKeyDown() &&
                !currentMiners.contains(stack)) {
            BlockVeinCache cache = new BlockVeinCache(level, pos, 4);
            cache.scanForBlocks();
            Queue<BlockPos> queuedPos = cache.getCache();
            if (player instanceof ServerPlayer serverPlayer) {
                while (!queuedPos.isEmpty() && !stack.isEmpty() &&
                        serverPlayer.hasCorrectToolForDrops(level.getBlockState(queuedPos.peek()))) {
                    if (!currentMiners.contains(stack)) currentMiners.add(stack);
                    cache.mine(queuedPos, serverPlayer);
                }
            }
            currentMiners.remove(stack);
            return false;
        } else return super.mineBlock(stack, level, state, pos, living);
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.BLOCK_FORTUNE) {
            int level = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
            int silkTouch = stack.getEnchantmentLevel(Enchantments.SILK_TOUCH);
            if (silkTouch > 0) return 0;
            else return Math.max(level, 1);
        }
        return super.getEnchantmentLevel(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        MutableComponent ench = Enchantments.BLOCK_FORTUNE.getFullname(1).copy();
        int silkTouchLevel = pStack.getEnchantmentLevel(Enchantments.SILK_TOUCH);
        if (silkTouchLevel <= 0)
            pTooltipComponents.add(Component.translatable(ModConstants.TRANSLATE_KEY_PREFIX.withSuffix("item_ench_text"), ench));
    }
}