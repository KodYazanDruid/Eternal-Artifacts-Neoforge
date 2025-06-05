package com.sonamorningstar.eternalartifacts.content.item.base;

import net.minecraft.world.item.Item;

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
		return new PipeFilterMenu(id, inv, player.hit);
	}*/
}
