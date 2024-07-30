package com.sonamorningstar.eternalartifacts.content.item;

import com.sonamorningstar.eternalartifacts.capabilities.ModItemItemStorage;
import com.sonamorningstar.eternalartifacts.container.KnapsackMenu;
import com.sonamorningstar.eternalartifacts.content.item.base.IOpenMenus;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class KnapsackItem extends Item implements IOpenMenus {
    public KnapsackItem(Properties pProperties) {
        super(pProperties);
    }

    public ModItemItemStorage createCapability(ItemStack stack) {
        int effLvl = stack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
        int size = (1 + effLvl) * 9;
        //int size = 36;
        return new ModItemItemStorage(stack, size);
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
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return Enchantments.BLOCK_EFFICIENCY == enchantment;
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
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new KnapsackMenu(id, inv, stack), stack.getHoverName()));
    }

}