package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.util.PlayerHelper;
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
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        IItemHandler ih = stack.getCapability(Capabilities.ItemHandler.ITEM);
        //TODO: Move this to helper method to use somewhere else.
        if(ih != null) {
            for (int i = 0; i < ih.getSlots(); i++) {
                int x = i % 9;
                int y = i / 9;
                addSlot(new SlotItemHandler(ih, i, 8 + x * 18, 18 + y * 18));
            }
        }
    }

    public static KnapsackMenu fromNetwork(int id, Inventory inv) {
        return new KnapsackMenu(id, inv, ModItems.KNAPSACK.toStack());
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            //Clicked from player inventory
            if (pIndex < 36) {
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
