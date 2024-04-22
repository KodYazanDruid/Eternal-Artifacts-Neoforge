package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.core.ModTiers;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
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

import java.util.List;

import static com.sonamorningstar.eternalartifacts.EternalArtifacts.MODID;

public class AxeOfRegrowthItem extends AxeItem {
    public AxeOfRegrowthItem(Properties pProperties) {
        super(ModTiers.CHLOROPHYTE, 5.0F, -3.0F, pProperties);
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

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        //Compat for mods allowing fortune enchantment level higher than 3.
        int level = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
        return enchantment == Enchantments.BLOCK_FORTUNE ? Math.max(level, 3) : 0;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("key."+MODID+".axe_of_regrowth_ench_text", Enchantments.BLOCK_FORTUNE.getFullname(3)));
    }
}
