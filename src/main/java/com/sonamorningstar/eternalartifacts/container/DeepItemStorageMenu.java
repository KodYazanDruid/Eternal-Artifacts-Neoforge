package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.capabilities.item.DeepItemStorageHandler;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.container.slot.DeepStorageSlotItemHandler;
import com.sonamorningstar.eternalartifacts.content.block.entity.DeepItemStorageUnit;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class DeepItemStorageMenu extends AbstractModContainerMenu {
	private final DeepItemStorageUnit dsu;
	public DeepItemStorageMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
		this(id, inventory, buf.readBlockPos());
		
	}
	public DeepItemStorageMenu(int id, Inventory inventory, BlockPos pos) {
		super(ModMenuTypes.DEEP_ITEM_STORAGE_UNIT.get(), id, inventory);
		Level level = inventory.player.level();
		this.dsu = ((DeepItemStorageUnit) level.getBlockEntity(pos));
		addPlayerInventoryAndHotbar(inventory, 8, 66);
		
		IItemHandler ih = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
		if (ih instanceof DeepItemStorageHandler dish) addSlot(new DeepStorageSlotItemHandler(dish, 0, 80, 30));
	}
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(dsu.getLevel(), dsu.getBlockPos()), player, dsu.getBlockState().getBlock());
	}
	
	@Override
	public void clicked(int slotId, int button, ClickType type, Player player) {
		ItemStack stack = getSlot(36).getItem();
		if (slotId >= 0 && slotId < 36 && handleShiftClickOnPlayerInv(type, slotId, player)) return;
		if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
			super.clicked(slotId, button, type, player);
			return;
		}
		if (slotId == 36 && handleOverFlowedStack(type, button, player)) return;
		super.clicked(slotId, button, type, player);
	}
	
	protected boolean handleShiftClickOnPlayerInv(ClickType type, int slotId, Player player) {
		Slot slot = getSlot(slotId);
		Slot targetSlot = getSlot(36);
		ItemStack toMove = slot.getItem();
		ItemStack stored = targetSlot.getItem();
		if (type == ClickType.QUICK_MOVE) {
			if (targetSlot.mayPickup(player) && (
				ItemStack.isSameItemSameTags(toMove, stored) || stored.isEmpty()
			) && !toMove.isEmpty()) {
				int space = targetSlot.getMaxStackSize() - stored.getCount();
				int toTransfer = Math.min(space, toMove.getCount());
				if (stored.isEmpty()) targetSlot.setByPlayer(toMove.copyWithCount(toTransfer));
				else stored.grow(toTransfer);
				toMove.shrink(toTransfer);
				slot.setChanged();
				return true;
			}
		}
		return false;
	}
	
	protected boolean handleOverFlowedStack(ClickType type, int button, Player player) {
		Slot slot = getSlot(36);
		ItemStack stack = slot.getItem();
		ItemStack carried = getCarried();
		if (type == ClickType.PICKUP) {
			//Transferring out of te DSU.
			if (carried.isEmpty()) {
				ItemStack copy = button == 1 ? stack.copyWithCount(stack.getMaxStackSize() / 2) : stack.copyWithCount(stack.getMaxStackSize());
				stack.shrink(copy.getCount());
				setCarried(copy);
				slot.setChanged();
				//Transferring into the DSU.
			} else if (ItemStack.isSameItemSameTags(stack, carried)) {
				int space = slot.getMaxStackSize() - stack.getCount();
				int toTransfer = Math.min(space, carried.getCount());
				stack.grow(toTransfer);
				setCarried(carried.copyWithCount(carried.getCount() - toTransfer));
				slot.setChanged();
			}
			return true;
		//Shift clicking
		} else if (type == ClickType.QUICK_MOVE) {
			if (!slot.mayPickup(player)) return false;
			ItemStack vanillaDefaultMax = stack.copyWithCount(stack.getMaxStackSize());
			moveItemStackTo(vanillaDefaultMax, 0, 36, false);
			stack.shrink(stack.getMaxStackSize() - vanillaDefaultMax.getCount());
			slot.setChanged();
			return true;
		}
		return false;
	}
}
