package com.sonamorningstar.eternalartifacts.content.tabs;

import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.core.ModInventoryTabs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.Nullable;

public class EnderChestTab extends AbstractInventoryTab {
    public EnderChestTab(FriendlyByteBuf data) {
        super(ModInventoryTabs.ENDER_KNAPSACK.get(), data);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        PlayerEnderChestContainer con = player.getEnderChestInventory();
        return new SimpleMenuProvider((id1, inv1, player1) -> ChestMenu.threeRows(id, inv, con), Component.empty()).createMenu(id, inv, player);
    }
}
