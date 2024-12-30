package com.sonamorningstar.eternalartifacts.content.tabs;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.container.KnapsackMenu;
import com.sonamorningstar.eternalartifacts.content.item.KnapsackItem;
import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.core.ModDataAttachments;
import com.sonamorningstar.eternalartifacts.core.ModInventoryTabs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class KnapsackTab extends AbstractInventoryTab {
    public KnapsackTab(FriendlyByteBuf data) {
        super(ModInventoryTabs.KNAPSACK.get(), data);
    }

    @Nullable
    @Override
    public Consumer<FriendlyByteBuf> getBytes(Player player) {
        CharmStorage charms = player.getData(ModDataAttachments.CHARMS);
        ItemStack stack = charms.getStackInSlot(9);
        return wr -> wr.writeItem(stack);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        CharmStorage charms = player.getData(ModDataAttachments.CHARMS);
        ItemStack stack = charms.getStackInSlot(9);
        if (stack.getItem() instanceof KnapsackItem) {
            return new KnapsackMenu(id, inv, stack) {
                @Override
                public boolean stillValid(Player player) {
                    return !player.isDeadOrDying() && charms.contains(stack.getItem());
                }
            };
        }
        return null;
    }
}
