package com.sonamorningstar.eternalartifacts.container;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.AbstractFluidTank;
import com.sonamorningstar.eternalartifacts.container.base.AbstractModContainerMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.DeepFluidStorageUnit;
import com.sonamorningstar.eternalartifacts.container.slot.FluidSlot;
import com.sonamorningstar.eternalartifacts.core.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class DeepFluidStorageMenu extends AbstractModContainerMenu {
	public final DeepFluidStorageUnit dsu;
	
	public DeepFluidStorageMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
		this(id, inventory, buf.readBlockPos());
	}
	
	public DeepFluidStorageMenu(int id, Inventory inventory, BlockPos pos) {
		super(ModMenuTypes.DEEP_FLUID_STORAGE_UNIT.get(), id, inventory);
		Level level = inventory.player.level();
		this.dsu = ((DeepFluidStorageUnit) level.getBlockEntity(pos));
		addPlayerInventoryAndHotbar(inventory, 8, 66);
		
		IFluidHandler fh = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null);
		if (fh instanceof AbstractFluidTank aft) addFluidSlot(new FluidSlot(() -> aft, 0, 80, 30));
	}
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(dsu.getLevel(), dsu.getBlockPos()), player, dsu.getBlockState().getBlock());
	}
	
}
