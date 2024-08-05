package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.Config;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class KnapsackMenu extends AbstractModContainerMenu {
    final ItemStack stack;

    public KnapsackMenu(int containerId, Inventory inv, ItemStack stack) {
        super(ModMenuTypes.KNAPSACK.get(), containerId);
        this.stack = stack;
        IItemHandler ih = stack.getCapability(Capabilities.ItemHandler.ITEM);
        int column = Config.KNAPSACK_SLOT_IN_ROW.get();
        int playerInvPadding = Math.max(0, column - 9) * 9;
        //TODO: Move this to helper method to use somewhere else.
        if(ih != null) {
            addPlayerInventoryAndHotbar(inv, 8 + playerInvPadding, (Mth.ceil((float) ih.getSlots() / column) * 18) + 12);
            for (int i = 0; i < ih.getSlots(); i++) {
                int x = i % column;
                int y = i / column;
                addSlot(new SlotItemHandler(ih, i, 8 + x * 18, 18 + y * 18));
            }
        }
    }

    public static KnapsackMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf extraData) {
        return new KnapsackMenu(id, inv, extraData.readItem());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        //System.out.println("Slot index: "+index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            //Clicked from player inventory
            if (index < 36) {
                 if (!this.moveItemStackTo(itemstack1, 36, this.slots.size(), false)) {
                     return ItemStack.EMPTY;
                 }
                //Clicked from opened container
            } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        /*ItemStack stack = player.getMainHandItem();
        return stack.is(ModItems.KNAPSACK);*/
        return PlayerHelper.findStack(player, stack);
    }
}
