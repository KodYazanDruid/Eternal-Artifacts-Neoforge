package com.sonamorningstar.eternalartifacts.client.gui.screen;

import com.sonamorningstar.eternalartifacts.client.gui.screen.base.AbstractModContainerScreen;
import com.sonamorningstar.eternalartifacts.container.BlueprintMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlotItemHandler;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.network.BlueprintReloadNbtToServer;
import com.sonamorningstar.eternalartifacts.network.Channel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
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
        renderTooltip(gui, mX, mY);
    }

/*    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);
        if (slot instanceof FakeSlotItemHandler fakeSlot) {
            ItemStack carried = menu.getCarried();
            ItemStack blueprint = menu.getBlueprint();
            if (carried.isEmpty()) {
                ItemStack stack = slot.getItem();
                if (!stack.isEmpty()) {
                    var list = BlueprintItem.getFakeItems(blueprint);
                    list.set(fakeSlot.getSlotIndex(), ItemStack.EMPTY);
                    updateItem(menu.getBlueprint(), list);
                    fakeSlot.set(ItemStack.EMPTY);
                }
            } else {
                ItemStack stack = carried.copyWithCount(1);
                var list = BlueprintItem.getFakeItems(blueprint);
                list.set(fakeSlot.getSlotIndex(), stack);
                updateItem(blueprint, list);
                fakeSlot.set(stack);
            }
        }
    }*/

/*    private void updateItem(ItemStack stack, NonNullList<ItemStack> items) {
        //BlueprintItem.updateFakeItems(stack, items);
        Channel.sendToServer(new BlueprintReloadNbtToServer(stack, items));
    }*/
}
