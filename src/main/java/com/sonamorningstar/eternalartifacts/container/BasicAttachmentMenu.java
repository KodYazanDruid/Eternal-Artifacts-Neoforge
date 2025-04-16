package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

@Getter
public class BasicAttachmentMenu extends AbstractModContainerMenu {
	private final BlockPos pos;
	private final Direction dir;
	private SimpleContainer fakeSlots;
	public BasicAttachmentMenu(int id, Inventory inv, BlockPos pos, Direction dir) {
		super(ModMenuTypes.BASIC_ATTACHMENT.get(), id);
		this.pos = pos;
		this.dir = dir;
		this.fakeSlots = new SimpleContainer(9);
		this.fakeSlots.addListener(this::slotsChanged);
		addPlayerInventoryAndHotbar(inv, 8, 66);
		addFakeSlots(62, 17);
	}
	
	private void addFakeSlots(int xOff, int yOff) {
		for (int i = 0; i < 9; i++) {
			addSlot(new FakeSlot(fakeSlots, i, xOff + (i % 3) * 18, yOff + (i / 3) * 18, false));
		}
	}
	
	@Override
	public void fakeSlotSynch(UpdateFakeSlotToServer pkt) {
		fakeSlots.setItem(pkt.index(), pkt.slotItem());
	}
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(player.level(), pos), player, player.level().getBlockState(pos).getBlock());
	}
	
	public static BasicAttachmentMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
		return new BasicAttachmentMenu(id, inv, buf.readBlockPos(), buf.readEnum(Direction.class));
	}
	
	@Override
	public void slotsChanged(Container con) {
		super.slotsChanged(con);
	}
	
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int index) {
		return ItemStack.EMPTY;
	}
}
