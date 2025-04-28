package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.FakeSlot;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.AbstractPipeBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import com.sonamorningstar.eternalartifacts.network.UpdateFakeSlotToServer;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@Getter
public class BasicAttachmentMenu extends AbstractModContainerMenu {
	private final BlockPos pos;
	private final Direction dir;
	private final int attType;
	private final AbstractPipeBlockEntity<?> pipe;
	private SimpleContainer fakeSlots;
	public BasicAttachmentMenu(int id, Inventory inv, BlockPos pos, Direction dir, int type) {
		super(ModMenuTypes.BASIC_ATTACHMENT.get(), id);
		this.pos = pos;
		this.dir = dir;
		this.attType = type;
		this.pipe = (AbstractPipeBlockEntity<?>) inv.player.level().getBlockEntity(pos);
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
		return new BasicAttachmentMenu(id, inv, buf.readBlockPos(), buf.readEnum(Direction.class), buf.readVarInt());
	}
	
	@Override
	public void slotsChanged(Container con) {
		super.slotsChanged(con);
		/*var filters = pipe.filters;
		var ing = NonNullList.withSize(9, Ingredient.EMPTY);
		for (int i = 0; i < con.getContainerSize(); i++) {
			ItemStack stack = con.getItem(i);
			if (stack.isEmpty()) {
				ing.set(i, Ingredient.EMPTY);
			} else {
				ing.set(i, Ingredient.of(stack));
			}
		}
		filters.put(dir, ing);*/
	}
	
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int index) {
		return ItemStack.EMPTY;
	}
}
