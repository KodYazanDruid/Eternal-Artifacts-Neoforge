package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.PictureScreen;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.network.SendStringToServer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class PictureScreenMenu extends AbstractModContainerMenu {
	public final PictureScreen screen;
	public PictureScreenMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		this(id, inv, (PictureScreen) inv.player.level().getBlockEntity(extraData.readBlockPos()));
	}
	
	public PictureScreenMenu(int id, Inventory inv, PictureScreen screen) {
		super(ModMenuTypes.PICTURE_SCREEN.get(), id, inv);
		this.screen = screen;
	}
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(player.level(), screen.getBlockPos()), player, screen.getBlockState().getBlock());
	}
	
	@Override
	public void receiveStringPkt(SendStringToServer pkt) {
		screen.setImageUrl(pkt.link());
	}
}
