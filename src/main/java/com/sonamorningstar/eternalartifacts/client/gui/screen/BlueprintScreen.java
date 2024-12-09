package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.client.gui.screen.util.GuiDrawer;
import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.network.BlueprintUpdateSlotToServer;
import com.sonamorningstar.eternalartifacts.network.Channel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BlueprintScreen extends AbstractModContainerScreen<BlueprintMenu> {
    public BlueprintScreen(BlueprintMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    public void render(GuiGraphics gui, int mX, int mY, float partTick) {
        super.render(gui, mX, mY, partTick);
        GuiDrawer.drawEmptyArrow(gui, leftPos + 90, topPos + 35);
        renderTooltip(gui, mX, mY);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);
        if (slot instanceof FakeSlot fakeSlot && !fakeSlot.isDisplayOnly()) {
            ItemStack carried = menu.getCarried();
            if (carried.isEmpty()) {
                ItemStack stack = slot.getItem();
                if (!stack.isEmpty()) {
                    updateItem(menu.containerId, fakeSlot.getSlotIndex(), ItemStack.EMPTY);
                    fakeSlot.set(ItemStack.EMPTY);
                }
            } else {
                ItemStack stack = carried.copyWithCount(1);
                updateItem(menu.containerId, fakeSlot.getSlotIndex(), stack);
                fakeSlot.set(stack);
            }
        }
    }

    private void updateItem(int menuId, int slotIndex, ItemStack stack) {
        Channel.sendToServer(new BlueprintUpdateSlotToServer(menuId, slotIndex, stack));
    }
}
