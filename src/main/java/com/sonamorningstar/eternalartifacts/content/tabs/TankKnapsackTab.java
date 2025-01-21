package com.sonamorningstar.eternalartifacts.content.tabs;

import com.sonamorningstar.eternalartifacts.api.charm.CharmStorage;
import com.sonamorningstar.eternalartifacts.api.charm.PlayerCharmManager;
import com.sonamorningstar.eternalartifacts.container.TankKnapsackMenu;
import com.sonamorningstar.eternalartifacts.content.item.TankKnapsackItem;
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

public class TankKnapsackTab extends AbstractInventoryTab {
    public TankKnapsackTab(FriendlyByteBuf data) {
        super(ModInventoryTabs.TANK_KNAPSACK.get(), data);
    }

    @Nullable
    @Override
    public Consumer<FriendlyByteBuf> getBytes(Player player) {
        ItemStack stack = PlayerCharmManager.findCharm(player, TankKnapsackItem.class);
        return wr -> wr.writeItem(stack);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        CharmStorage charms = player.getData(ModDataAttachments.CHARMS);
        ItemStack stack = PlayerCharmManager.findCharm(player, TankKnapsackItem.class);
        if (stack.getItem() instanceof TankKnapsackItem) {
            return new TankKnapsackMenu(id, inv, stack) {
                @Override
                public boolean stillValid(Player player1) {
                    return !player1.isDeadOrDying() && charms.contains(stack.getItem());
                }
            };
        }
        return null;
    }
}
