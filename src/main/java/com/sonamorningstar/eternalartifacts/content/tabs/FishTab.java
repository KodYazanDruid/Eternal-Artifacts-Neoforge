package com.sonamorningstar.eternalartifacts.content.tabs;

import com.sonamorningstar.eternalartifacts.container.FishMenu;
import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.core.ModInventoryTabs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class FishTab extends AbstractInventoryTab {
    public FishTab(FriendlyByteBuf data) {
        super(ModInventoryTabs.FISH_TAB.get(), data);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FishMenu(pContainerId, pPlayerInventory, data);
    }
}
