package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.capabilities.handler.IItemCooldown;
import com.sonamorningstar.eternalartifacts.core.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends DiggerItem {
    public WrenchItem(Properties props) {super(2F, -2F, Tiers.IRON, ModTags.Blocks.MINEABLE_WITH_WRENCH, props); }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
        if(state.is(ModTags.Blocks.MINEABLE_WITH_WRENCH)) level.playSound(null, pos, ModSounds.WRENCH.get(), SoundSource.BLOCKS, 1, 1);
        return super.mineBlock(stack, level, state, pos, living);
    }

    //Testing and debugging stuff.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        IItemCooldown itemCooldown = stack.getCapability(ModCapabilities.ItemCooldown.ITEM);
        if (itemCooldown != null && !itemCooldown.isOnCooldown()) {
            itemCooldown.setCooldown(100);
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.literal("bomba"));
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        BlockState state = level.getBlockState(pos);

        return super.useOn(ctx);
    }
}
