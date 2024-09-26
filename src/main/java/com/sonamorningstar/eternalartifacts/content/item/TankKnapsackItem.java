package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.container.TankKnapsackMenu;
import com.sonamorningstar.eternalartifacts.content.item.base.VolumeHolderItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TankKnapsackItem extends VolumeHolderItem {
    public TankKnapsackItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack knapsack = player.getItemInHand(hand);
            openMenu(player, knapsack);
            return InteractionResultHolder.sidedSuccess(knapsack, level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    private void openMenu(Player player, ItemStack stack) {
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new TankKnapsackMenu(id, inv, stack), stack.getHoverName()), buff -> buff.writeItem(stack));
    }
}