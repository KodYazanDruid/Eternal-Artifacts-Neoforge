package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.container.KnapsackMenu;
import com.sonamorningstar.eternalartifacts.content.item.base.VolumeHolderItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class KnapsackItem extends VolumeHolderItem {
    public KnapsackItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player) && other.isEmpty()){
            if(!player.level().isClientSide()) openMenu(player, stack);
            return true;
        } else {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }else{
            openMenu(player, stack);
            return InteractionResultHolder.consume(stack);
        }
    }

    private void openMenu(Player player, ItemStack stack) {
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new KnapsackMenu(id, inv, stack), stack.getHoverName()), buff -> buff.writeItem(stack));
    }
}