package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.content.item.PortableBatteryItem;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PortableBatteryMenu extends TabMenu {
    public final ItemStack battery;
    public PortableBatteryMenu(int id, Inventory inv, ItemStack battery) {
        super(ModMenuTypes.PORTABLE_BATTERY.get(), id, inv);
        this.battery = battery;
        addPlayerInventoryAndHotbar(inv, 8, 66);
    }

    public static PortableBatteryMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf extraData) {
        return new PortableBatteryMenu(id, inv, extraData.readItem());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        toggle(player, id);
        return super.clickMenuButton(player, id);
    }

    public void toggle(Player player, int id) {
        ItemStack stack = CharmManager.findCharm(player, PortableBatteryItem.class);
        PortableBatteryItem.SlotType.values()[id].toggle(stack);
    }
}
