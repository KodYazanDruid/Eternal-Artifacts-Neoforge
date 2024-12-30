package com.sonamorningstar.eternalartifacts.content.tabs;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.core.ModInventoryTabs;
import com.sonamorningstar.eternalartifacts.core.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jetbrains.annotations.Nullable;

public class CrafterTab extends AbstractInventoryTab {
    public CrafterTab(FriendlyByteBuf data) {
        super(ModInventoryTabs.CRAFTER.get(), data);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new CraftingMenu(id, inv, ContainerLevelAccess.create(player.level(), player.blockPosition())){
            @Override
            public boolean stillValid(Player player) {
                CharmStorage charms = player.getData(ModDataAttachments.CHARMS);
                return !player.isDeadOrDying() && charms.contains(ModItems.PORTABLE_CRAFTER.get());
            }
        };
    }
}
