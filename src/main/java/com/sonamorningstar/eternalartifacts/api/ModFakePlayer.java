package com.sonamorningstar.eternalartifacts.api;

import com.mojang.authlib.GameProfile;
import com.sonamorningstar.eternalartifacts.capabilities.item.ModItemStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.Machine;
import com.sonamorningstar.eternalartifacts.util.ItemHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
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
	
	public void setFoodData(FoodData foodData) {
		this.foodData = foodData;
	}
	
	@Override
	public double getEyeY() {
		float xRot = getXRot();
		double offset = xRot == 90.0F ? -0.5D : xRot == -90.0F ? 0.5D : 0.0D;
		return machine.getBlockPos().getY() + 0.5D + offset;
	}
	
	@Override
	public void spawnItemParticles(ItemStack stack, int amount) {
		for(int i = 0; i < amount; ++i) {
			Vec3 vec3 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3 = vec3.xRot(-this.getXRot() * (float) (Math.PI / 180.0));
			vec3 = vec3.yRot(-this.getYRot() * (float) (Math.PI / 180.0));
			double d0 = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3 vec31 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.3, d0, 0.6);
			vec31 = vec31.xRot(-this.getXRot() * (float) (Math.PI / 180.0));
			vec31 = vec31.yRot(-this.getYRot() * (float) (Math.PI / 180.0));
			vec31 = vec31.add(this.getX(), this.getEyeY(), this.getZ());
			((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), vec31.x, vec31.y, vec31.z, 0, vec3.x, vec3.y + 0.05, vec3.z, 1.0);
		}
	}
	
	@Override
	public boolean canBeAffected(MobEffectInstance effectInstance) { return false; }
	
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
			} else {
				super.setItem(slot, stack);
			}
		}
		
		@Override
		public boolean add(ItemStack stack) {
			ItemStack remaining = ItemHelper.insertItemStackedForced(fakePlayer.machine.inventory, stack, false);
			return remaining.isEmpty();
		}
		
		@Override
		public boolean add(int slot, ItemStack stack) {
			if (slot == -1) return this.add(stack);
			ItemStack remainder = fakePlayer.machine.inventory.insertItemForced(slot, stack, false);
			return remainder.isEmpty();
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
		private final ModItemStorage machineInv;
		private final int size;
		
		public SyncedItemList(ModItemStorage machineInv, int size) {
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
				ItemStack old = machineInv.getStackInSlot(index).copy();
				machineInv.setStackInSlot(index, element);
				return old;
			}
			return ItemStack.EMPTY;
		}
		
		@Override
		public void add(int index, ItemStack value) {
			if (index >= 0 && index < size) {
				machineInv.insertItemForced(index, value, false);
			}
		}
		
		@Override
		public ItemStack remove(int index) {
			if (index >= 0 && index < size) {
				int count = machineInv.getStackInSlot(index).getCount();
				return machineInv.extractItem(index, count, false);
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
