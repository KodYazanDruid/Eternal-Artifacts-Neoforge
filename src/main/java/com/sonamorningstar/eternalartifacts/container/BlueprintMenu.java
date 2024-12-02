package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlotItemHandler;
import com.sonamorningstar.eternalartifacts.content.item.BlueprintItem;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

@Getter
public class BlueprintMenu extends AbstractModContainerMenu {
    private final ItemStack blueprint;
    public BlueprintMenu(int id, Inventory inv, ItemStack blueprint) {
        super(ModMenuTypes.BLUEPRINT.get(), id);
        this.blueprint = blueprint;
        addPlayerInventoryAndHotbar(inv, 8, 66);
        addFakeSlots(44, 18);
    }

    public static BlueprintMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf extraData) {
        return new BlueprintMenu(id, inv, extraData.readItem());
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isDeadOrDying();
    }

    private void addFakeSlots(int xOff, int yOff) {
        if (blueprint.getItem() instanceof BlueprintItem) {
            IItemHandler handler = blueprint.getCapability(Capabilities.ItemHandler.ITEM);
            for (int i = 0; i < 9; i++) {
                addSlot(new FakeSlotItemHandler(handler, i, xOff + (i % 3) * 18, yOff + (i / 3) * 18));
            }
        }
    }

    @Override
    public void slotsChanged(Container pContainer) {
        super.slotsChanged(pContainer);
    }
}
