package com.sonamorningstar.eternalartifacts.content.item.base;

import com.sonamorningstar.eternalartifacts.container.BasicAttachmentMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public abstract class AttachmentItem extends Item /*implements MenuProvider */{
	public AttachmentItem(Properties props) {
		super(props);
	}
	
	/*@Override
	public Component getDisplayName() {
		return getDescription();
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new BasicAttachmentMenu(id, inv, player.hit);
	}*/
}
