package com.sonamorningstar.eternalartifacts.content.block.entity;

import com.sonamorningstar.eternalartifacts.capabilities.fluid.ModFluidStorage;
import com.sonamorningstar.eternalartifacts.container.DeepFluidStorageMenu;
import com.sonamorningstar.eternalartifacts.container.base.AbstractMachineMenu;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.IDeepStorage;
import com.sonamorningstar.eternalartifacts.content.block.entity.base.ModBlockEntity;
import com.sonamorningstar.eternalartifacts.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DeepFluidStorageUnit extends ModBlockEntity implements IDeepStorage, MenuProvider {
	public final ModFluidStorage tank;
	public DeepFluidStorageUnit(BlockPos pos, BlockState state) {
		super(ModBlockEntities.DEEP_FLUID_STORAGE_UNIT.get(), pos, state);
		this.tank = createBasicTank(Integer.MAX_VALUE);
	}
	
	@Override
	protected boolean shouldSyncOnUpdate() { return true; }
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		tank.deserializeNBT(tag.getCompound("Fluid"));
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Fluid", tank.serializeNBT());
	}
	
	@Override
	public InteractionResult useStorageBlock(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		AbstractMachineMenu.openContainer(player, pos);
		return InteractionResult.sidedSuccess(level.isClientSide());
	}
	
	@Override
	public Component getDisplayName() {
		return getBlockState().getBlock().getName();
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new DeepFluidStorageMenu(pContainerId, pPlayerInventory, this.worldPosition);
	}
}
