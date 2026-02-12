package com.sonamorningstar.eternalartifacts.api;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;

@Getter
public class ModFakePlayer extends FakePlayer {
	private final Machine<?> machine;
	public ModFakePlayer(ServerLevel level, GameProfile name, @Nullable Machine<?> machine) throws NoSuchFieldException, IllegalAccessException {
		super(level, name);
		this.machine = machine;
		if (machine != null) {
			Field inventory = Player.class.getDeclaredField("inventory");
			inventory.setAccessible(true);
			inventory.set(this, new ModFakePlayerInventory(this));
		}
	}
	
	@Override
	public void initMenu(AbstractContainerMenu pMenu) {}
	
	@Override
	protected boolean canRide(Entity vehicle) { return false; }
	
	@Override
	public boolean startRiding(Entity vehicle, boolean force) {
		return false;
	}
	
	@Override
	public OptionalInt openMenu(@Nullable MenuProvider pMenu) {
		return OptionalInt.empty();
	}
	
	@Override
	public OptionalInt openMenu(MenuProvider menuProvider, BlockPos pos) {
		return OptionalInt.empty();
	}
	
	@Override
	public OptionalInt openMenu(@Nullable MenuProvider pMenu, @Nullable Consumer<FriendlyByteBuf> extraDataWriter) {
		return OptionalInt.empty();
	}
	
	public static class ModFakePlayerInventory extends Inventory {
		@Getter
		private final ModFakePlayer fakePlayer;
		private final int slotCount;
		
		public ModFakePlayerInventory(ModFakePlayer player) throws NoSuchFieldException, IllegalAccessException {
			super(player);
			this.fakePlayer = player;
			this.slotCount = player.machine.inventory.getSlots();
			
			NonNullList<ItemStack> syncedItems = new SyncedItemList(player.machine.inventory, slotCount);
			
			Field itemsField = Inventory.class.getDeclaredField("items");
			itemsField.setAccessible(true);
			itemsField.set(this, syncedItems);
			
			Field compartmentsField = Inventory.class.getDeclaredField("compartments");
			compartmentsField.setAccessible(true);
			List<NonNullList<ItemStack>> newCompartments = List.of(syncedItems, this.armor, this.offhand);
			compartmentsField.set(this, newCompartments);
		}
		
		@Override
		public int getContainerSize() {
			return slotCount;
		}
		
		@Override
		public ItemStack getItem(int slot) {
			if (slot >= 0 && slot < slotCount) {
				return fakePlayer.machine.inventory.getStackInSlot(slot);
			}
			return super.getItem(slot);
		}
		
		@Override
		public void setItem(int slot, ItemStack stack) {
			if (slot >= 0 && slot < slotCount) {
				fakePlayer.machine.inventory.setStackInSlot(slot, stack);
				this.items.set(slot, stack);
			} else {
				super.setItem(slot, stack);
			}
		}
		
		@Override
		public ItemStack removeItem(int slot, int amount) {
			if (slot >= 0 && slot < slotCount) {
				ItemStack stack = fakePlayer.machine.inventory.getStackInSlot(slot);
				if (!stack.isEmpty()) {
					ItemStack extracted = fakePlayer.machine.inventory.extractItem(slot, amount, false);
					this.setChanged();
					return extracted;
				}
				return ItemStack.EMPTY;
			}
			return super.removeItem(slot, amount);
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int slot) {
			if (slot >= 0 && slot < slotCount) {
				ItemStack stack = fakePlayer.machine.inventory.getStackInSlot(slot);
				fakePlayer.machine.inventory.setStackInSlot(slot, ItemStack.EMPTY);
				return stack;
			}
			return super.removeItemNoUpdate(slot);
		}
		
		@Override
		public ItemStack getSelected() {
			return this.items.get(this.selected);
		}
	}
	
	/**
	 * Makine envanterini saran NonNullList implementasyonu.
	 * get/set işlemleri doğrudan makine envanterine yönlendirilir.
	 */
	private static class SyncedItemList extends NonNullList<ItemStack> {
		private final com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage machineInv;
		private final int size;
		
		public SyncedItemList(com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage machineInv, int size) {
			super(Collections.nCopies(size, ItemStack.EMPTY), ItemStack.EMPTY);
			this.machineInv = machineInv;
			this.size = size;
		}
		
		@Override
		public ItemStack get(int index) {
			if (index >= 0 && index < size) {
				return machineInv.getStackInSlot(index);
			}
			return ItemStack.EMPTY;
		}
		
		@Override
		public ItemStack set(int index, ItemStack element) {
			if (index >= 0 && index < size) {
				ItemStack old = machineInv.getStackInSlot(index);
				machineInv.setStackInSlot(index, element);
				return old;
			}
			return ItemStack.EMPTY;
		}
		
		@Override
		public int size() {
			return size;
		}
		
		@Override
		public boolean isEmpty() {
			for (int i = 0; i < size; i++) {
				if (!machineInv.getStackInSlot(i).isEmpty()) {
					return false;
				}
			}
			return true;
		}
	}
}
