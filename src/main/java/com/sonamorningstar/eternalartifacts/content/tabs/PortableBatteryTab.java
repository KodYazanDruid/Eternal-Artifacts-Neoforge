package com.sonamorningstar.eternalartifacts.content.tabs;

import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.container.PortableBatteryMenu;
import com.sonamorningstar.eternalartifacts.content.item.PortableBatteryItem;
import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.core.ModInventoryTabs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PortableBatteryTab extends AbstractInventoryTab {
    public PortableBatteryTab(FriendlyByteBuf data) {
        super(ModInventoryTabs.PORTABLE_BATTERY.get(), data);
    }

    @Nullable
    @Override
    public Consumer<FriendlyByteBuf> getBytes(Player player) {
        return wr -> wr.writeItem(PlayerCharmManager.findCharm(player, PortableBatteryItem.class));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new PortableBatteryMenu(id, inv, PlayerCharmManager.findCharm(player, PortableBatteryItem.class));
    }
}
