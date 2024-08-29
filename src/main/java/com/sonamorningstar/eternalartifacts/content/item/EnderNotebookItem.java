package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.content.item.base.VolumeHolderItem;
import com.sonamorningstar.eternalartifacts.network.Channel;
import com.sonamorningstar.eternalartifacts.network.EnderNotebookOpenToClient;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderNotebookItem extends VolumeHolderItem {
    public EnderNotebookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(player instanceof ServerPlayer serverPlayer) {
            Channel.sendToPlayer(new EnderNotebookOpenToClient(itemstack), serverPlayer);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
