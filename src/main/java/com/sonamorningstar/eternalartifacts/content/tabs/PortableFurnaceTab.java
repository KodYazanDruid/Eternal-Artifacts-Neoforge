package com.sonamorningstar.eternalartifacts.content.tabs;

import com.sonamorningstar.eternalartifacts.api.charm.CharmManager;
import com.sonamorningstar.eternalartifacts.container.PortableFurnaceMenu;
import com.sonamorningstar.eternalartifacts.content.item.PortableFurnaceItem;
import com.sonamorningstar.eternalartifacts.content.tabs.base.AbstractInventoryTab;
import com.sonamorningstar.eternalartifacts.core.ModInventoryTabs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PortableFurnaceTab extends AbstractInventoryTab {
	public PortableFurnaceTab(FriendlyByteBuf data) {super(ModInventoryTabs.PORTABLE_FURNACE.get(), data);}
	
	@Nullable
	@Override
	public Consumer<FriendlyByteBuf> getBytes(Player player) {
		return wr -> wr.writeItem(CharmManager.findCharm(player, PortableFurnaceItem.class));
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		ItemStack charm = CharmManager.findCharm(player, PortableFurnaceItem.class);
		return new PortableFurnaceMenu(id, inv, charm, PortableFurnaceItem.getContainerData(charm));
		
	}
}
