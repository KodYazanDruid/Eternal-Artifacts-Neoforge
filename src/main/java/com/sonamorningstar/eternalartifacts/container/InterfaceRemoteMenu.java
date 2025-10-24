package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.EternalArtifacts;
import com.sonamorningstar.eternalartifacts.client.gui.widget.Warp;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.recipe.inventory.FluidSlot;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

//FIXME: close the menu when block is broken
public class InterfaceRemoteMenu extends AbstractModContainerMenu {
	public final ItemStack remote;
	public final Warp bindedBlock;
	public final Direction side;
	public int invYOff = 4;
	public InterfaceRemoteMenu(int id, Inventory inv, ItemStack remote, Warp bindedBlock, Direction side) {
		super(ModMenuTypes.INTERFACE_REMOTE.get(), id, inv);
		this.remote = remote;
		this.bindedBlock = bindedBlock;
		this.side = side;
		try {
			Level interfaceLvl = inv.player.level();
			IItemHandler itemHandler = interfaceLvl.getCapability(Capabilities.ItemHandler.BLOCK, bindedBlock.getPosition(), side);
			IFluidHandler fluidHandler = interfaceLvl.getCapability(Capabilities.FluidHandler.BLOCK, bindedBlock.getPosition(), side);
			int totalCapSlots = (itemHandler instanceof IItemHandlerModifiable ? itemHandler.getSlots() : 0) + (fluidHandler != null ? fluidHandler.getTanks() : 0);
			/*invYOff += itemHandler != null ? Mth.ceil((float) itemHandler.getSlots() / 9) * 18 : 0;
			invYOff += fluidHandler != null ? Mth.ceil((float) fluidHandler.getTanks() / 9) * 18 : 0;*/
			invYOff += Mth.ceil((float) totalCapSlots / 9) * 18;
			addPlayerInventoryAndHotbar(inv, 8, 8 + invYOff);
			int slotIndex = 0;
			if (itemHandler instanceof IItemHandlerModifiable) {
				for (int i = 0; i < itemHandler.getSlots(); i++) {
					addSlot(new SlotItemHandler(itemHandler, i, 8 + (slotIndex % 9) * 18, 18 + (slotIndex / 9) * 18));
					slotIndex++;
				}
			}
			if (fluidHandler != null) {
				for (int i = 0; i < fluidHandler.getTanks(); i++) {
					addFluidSlot(new FluidSlot(fluidHandler, i, 7 + (slotIndex % 9) * 18, 17 + (slotIndex / 9) * 18));
					slotIndex++;
				}
			}
			/*IEnergyStorage energyStorage = interfaceLvl.getCapability(Capabilities.EnergyStorage.BLOCK, bindedBlock.getPosition(), null);
			if (energyStorage != null) {
				*//*addEnergySlot(8 + (slotIndex % 9) * 18, 18 + (slotIndex / 9) * 18, energyStorage);
				slotIndex++;*//*
			}*/
		} catch (IndexOutOfBoundsException e) {
			EternalArtifacts.LOGGER.error("Couldn't receive handlers on the client. {}", e.getMessage());
		}
	}
	
	@Override
	public boolean stillValid(Player player) {
		return !player.isDeadOrDying() && player.getInventory().contains(remote);
	}
	
	public static InterfaceRemoteMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
		return new InterfaceRemoteMenu(id, inv, buf.readItem(), Warp.readFromNBT(buf.readNbt()), buf.readEnum(Direction.class));
	}
}
